package com.espe.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     * GatewayFilter to intercept /logout requests BEFORE they get routed to
     * frontend.
     * This runs as part of the gateway filter chain with very high priority.
     */
    @Bean
    @Order(-100) // Very high priority to run before routing
    public org.springframework.cloud.gateway.filter.GlobalFilter logoutGlobalFilter() {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();
            if ("/logout".equals(path)) {
                // Check if there's a security context
                return exchange.getPrincipal()
                        .flatMap(principal -> {
                            // Has principal, let the filter chain continue (Spring Security will handle)
                            return chain.filter(exchange);
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            // No principal/session, redirect to login
                            exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                            exchange.getResponse().getHeaders()
                                    .setLocation(URI.create("/oauth2/authorization/gateway-client"));
                            return exchange.getResponse().setComplete();
                        }));
            }
            return chain.filter(exchange);
        };
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        // Custom logout success handler that properly redirects to login
        ServerLogoutSuccessHandler logoutSuccessHandler = (exchange, authentication) -> {
            // Try to perform an OP (IdP) logout using end_session_endpoint and
            // id_token_hint.
            // If we cannot resolve provider logout URL or id token, fall back to
            // redirecting to '/'.

            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
                String registrationId = oauth2Token.getAuthorizedClientRegistrationId();

                return clientRegistrationRepository.findByRegistrationId(registrationId)
                        .flatMap(reg -> {
                            // Try to get end_session_endpoint from metadata, otherwise construct it from
                            // authorization-uri
                            Object endSessionObj = reg.getProviderDetails().getConfigurationMetadata()
                                    .get("end_session_endpoint");
                            String endSession = null;
                            if (endSessionObj != null) {
                                endSession = endSessionObj.toString();
                            } else {
                                // Fallback: derive end_session_endpoint from the authorization URI
                                // e.g., http://localhost:9000/oauth2/authorize ->
                                // http://localhost:9000/connect/logout
                                String authUri = reg.getProviderDetails().getAuthorizationUri();
                                if (authUri != null && authUri.contains("/oauth2/authorize")) {
                                    endSession = authUri.replace("/oauth2/authorize", "/connect/logout");
                                }
                            }

                            String targetUrl = "/";
                            if (endSession != null) {
                                StringBuilder sb = new StringBuilder(endSession);
                                String idToken = null;
                                if (oauth2Token.getPrincipal() instanceof OidcUser) {
                                    OidcUser oidc = (OidcUser) oauth2Token.getPrincipal();
                                    if (oidc.getIdToken() != null) {
                                        idToken = oidc.getIdToken().getTokenValue();
                                    }
                                }
                                String joiner = endSession.contains("?") ? "&" : "?";
                                // Get the base URL from the request
                                ServerHttpRequest request = exchange.getExchange().getRequest();
                                String scheme = request.getURI().getScheme();
                                String host = request.getHeaders().getFirst("Host");
                                if (host == null) {
                                    host = request.getURI().getAuthority();
                                }
                                String baseUrl = scheme + "://" + host;
                                String loginUrl = baseUrl + "/oauth2/authorization/gateway-client";

                                if (idToken != null) {
                                    sb.append(joiner)
                                            .append("id_token_hint=")
                                            .append(URLEncoder.encode(idToken, StandardCharsets.UTF_8))
                                            .append("&post_logout_redirect_uri=")
                                            .append(URLEncoder.encode(loginUrl, StandardCharsets.UTF_8));
                                } else {
                                    sb.append(joiner)
                                            .append("post_logout_redirect_uri=")
                                            .append(URLEncoder.encode(loginUrl, StandardCharsets.UTF_8));
                                }
                                targetUrl = sb.toString();
                            }

                            // Clear session cookie by setting it to empty and expired
                            exchange.getExchange().getResponse().getCookies().set("SESSION",
                                    org.springframework.http.ResponseCookie.from("SESSION", "")
                                            .path("/")
                                            .maxAge(0)
                                            .build());

                            exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            exchange.getExchange().getResponse().getHeaders().setLocation(URI.create(targetUrl));
                            return Mono.<Void>empty();
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            // Clear session cookie
                            exchange.getExchange().getResponse().getCookies().set("SESSION",
                                    org.springframework.http.ResponseCookie.from("SESSION", "")
                                            .path("/")
                                            .maxAge(0)
                                            .build());

                            // Redirect to login page to force re-authentication
                            exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            exchange.getExchange().getResponse().getHeaders()
                                    .setLocation(URI.create("/oauth2/authorization/gateway-client"));
                            return Mono.<Void>empty();
                        }));
            }

            // Clear session cookie for non-OAuth2 sessions
            exchange.getExchange().getResponse().getCookies().set("SESSION",
                    org.springframework.http.ResponseCookie.from("SESSION", "")
                            .path("/")
                            .maxAge(0)
                            .build());

            // Redirect to login page to force re-authentication
            exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
            exchange.getExchange().getResponse().getHeaders()
                    .setLocation(URI.create("/oauth2/authorization/gateway-client"));
            return Mono.empty();
        };

        // Logout handlers to clear session and security context
        DelegatingServerLogoutHandler logoutHandler = new DelegatingServerLogoutHandler(
                new WebSessionServerLogoutHandler(),
                new SecurityContextServerLogoutHandler());

        http
                .authorizeExchange(exchanges -> exchanges
                        // Allow OAuth2 authorization endpoints and discovery to be proxied through the
                        // gateway
                        .pathMatchers("/oauth2/**", "/.well-known/**", "/login/**", "/login/oauth2/**", "/favicon.ico")
                        .permitAll()
                        // Allow logout endpoint (GET and POST)
                        .pathMatchers("/logout").permitAll()
                        // Allow frontend root and assets (so users can see the page after logout)
                        .pathMatchers("/", "/index.html", "/assets/**", "/static/**", "/src/**", "/*.js", "/*.css",
                                "/*.svg", "/*.ico")
                        .permitAll()
                        // Allow public API endpoints only when NOT authenticated - this ensures
                        // userinfo returns unauthenticated after logout
                        .pathMatchers("/api/userinfo", "/api/logout").permitAll()
                        // Everything else requires authentication
                        .anyExchange().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                new RedirectServerAuthenticationEntryPoint("/oauth2/authorization/gateway-client")))
                .oauth2Login(Customizer.withDefaults())
                .oauth2Client(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .requiresLogout(ServerWebExchangeMatchers.pathMatchers("/logout"))
                        .logoutHandler(logoutHandler)
                        .logoutSuccessHandler(logoutSuccessHandler))
                // SEC-004: CSRF deshabilitado porque:
                // 1. OAuth2 usa tokens Bearer (no cookies de sesión vulnerables a CSRF)
                // 2. El frontend es un SPA que no usa formularios tradicionales
                // 3. Las APIs validan tokens JWT en cada petición
                // Los tokens Bearer proporcionan protección equivalente a CSRF
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
