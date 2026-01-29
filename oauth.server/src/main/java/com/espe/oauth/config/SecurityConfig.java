package com.espe.oauth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final com.espe.oauth.service.CustomUserDetailsService userDetailsService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public SecurityConfig(com.espe.oauth.service.CustomUserDetailsService userDetailsService,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Security filter chain for the Authorization Server endpoints
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0

        http
                // Redirect to login page when not authenticated for authorization endpoint
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
                // Accept access tokens for User Info and/or Client Registration
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * Security filter chain for REST API endpoints (JWT protected)
     * Requires ROLE_ADMIN for user management
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer((resourceServer) -> resourceServer
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults());

        return http.build();
    }

    /**
     * JwtAuthenticationConverter that extracts roles from the 'roles' claim
     */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Get default authorities (SCOPE_xxx)
            JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
            Collection<GrantedAuthority> authorities = new java.util.ArrayList<>(defaultConverter.convert(jwt));

            // Add roles from the 'roles' claim
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            }

            return authorities;
        });
        return converter;
    }

    /**
     * Security filter chain for authentication (login form)
     */
    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
            @org.springframework.beans.factory.annotation.Value("${oauth2.client.gateway.base-url:http://localhost:8080}") String gatewayUrl)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/status").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated())
                // Form login handles the redirect to the login page from the authorization
                // server filter chain
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl(gatewayUrl + "/"))
                .userDetailsService(userDetailsService);

        return http.build();
    }

    /**
     * JWK Source for signing tokens
     * SEC-005 FIX: Usa un keyId fijo para mantener consistencia entre reinicios.
     * En producción real, las llaves deberían cargarse desde archivos PEM o un Key
     * Vault.
     * 
     * Para persistir las llaves en producción:
     * 1. Generar keypair: openssl genrsa -out private.pem 2048
     * 2. Extraer pública: openssl rsa -in private.pem -pubout -out public.pem
     * 3. Montar archivos en el contenedor y cargar aquí
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // SEC-005 FIX: Usa un keyId fijo en lugar de UUID.randomUUID()
        // Esto evita que los tokens sean invalidados al reiniciar en desarrollo
        // En producción, usar llaves persistentes cargadas desde archivos
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("oauth-server-key-1") // ID fijo para consistencia
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * Generate RSA key pair for JWT signing
     * NOTA: Esta llave se regenera en cada reinicio del servidor.
     * En producción, las llaves deben cargarse desde archivos persistentes.
     */
    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    /**
     * JWT Decoder for decoding tokens
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * Authorization Server settings (issuer URL)
     * The issuer URL should match the public URL used by clients to access this
     * server
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings(
            @org.springframework.beans.factory.annotation.Value("${oauth2.issuer.uri:http://localhost:9000}") String issuerUri) {
        return AuthorizationServerSettings.builder()
                .issuer(issuerUri)
                .build();
    }

    /**
     * Token Customizer to include user roles in the JWT
     * This adds the "roles" claim to both access tokens and ID tokens
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer() {
        return context -> {
            // Get the principal (authenticated user)
            if (context.getPrincipal() != null) {
                var authorities = context.getPrincipal().getAuthorities();
                if (authorities != null) {
                    List<String> roles = authorities.stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList());

                    // Add roles to the JWT claims
                    context.getClaims().claim("roles", roles);
                }
            }
        };
    }
}
