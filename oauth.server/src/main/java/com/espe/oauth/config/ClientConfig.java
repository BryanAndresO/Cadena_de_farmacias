package com.espe.oauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ClientConfig {

    @Value("${oauth2.client.gateway.base-url:http://localhost:8080}")
    private String gatewayBaseUrl;
    
    @Value("${oauth2.client.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;
    
    @Value("${oauth2.client.public.base-url:}")
    private String publicBaseUrl;

    /**
     * Configure OAuth2 clients (Gateway)
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
        // Gateway Client - Main application client
        RegisteredClient.Builder gatewayClientBuilder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("gateway-client")
                .clientSecret(passwordEncoder.encode("12345"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD) // Enabled for Postman
                // Dynamic redirect URIs based on environment
                .redirectUri(gatewayBaseUrl + "/login/oauth2/code/gateway-client")
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/gateway-client")
                .redirectUri("http://localhost:8080/login/oauth2/code/gateway-client")
                .redirectUri("https://oauth.pstmn.io/v1/callback") // Postman Web
                .redirectUri("https://oauth.pstmn.io/v1/browser-callback") // Postman Desktop
                .postLogoutRedirectUri(gatewayBaseUrl + "/oauth2/authorization/gateway-client")
                .postLogoutRedirectUri("http://127.0.0.1:8080/oauth2/authorization/gateway-client")
                .postLogoutRedirectUri("http://localhost:8080/oauth2/authorization/gateway-client");
        
        // Add public URL redirect URIs if configured
        if (publicBaseUrl != null && !publicBaseUrl.isEmpty()) {
            gatewayClientBuilder
                .redirectUri(publicBaseUrl + "/login/oauth2/code/gateway-client")
                .postLogoutRedirectUri(publicBaseUrl + "/oauth2/authorization/gateway-client");
        }
        
        RegisteredClient gatewayClient = gatewayClientBuilder
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("read")
                .scope("write")
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false) // No consent screen for development
                        .build())
                .build();

        // SPA public client (PKCE) - used by the frontend Single Page App
        RegisteredClient.Builder spaClientBuilder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("spa-client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                // Dynamic redirect URIs based on environment
                .redirectUri(frontendBaseUrl + "/oauth2/callback")
                .redirectUri(gatewayBaseUrl + "/oauth2/callback")
                .redirectUri("http://127.0.0.1:5173/oauth2/callback")
                .redirectUri("http://localhost:5173/oauth2/callback")
                .redirectUri("http://localhost:8080/oauth2/callback")
                .postLogoutRedirectUri(frontendBaseUrl + "/")
                .postLogoutRedirectUri("http://127.0.0.1:5173/")
                .postLogoutRedirectUri("http://localhost:5173/");
        
        // Add public URL redirect URIs if configured
        if (publicBaseUrl != null && !publicBaseUrl.isEmpty()) {
            spaClientBuilder
                .redirectUri(publicBaseUrl + "/oauth2/callback")
                .postLogoutRedirectUri(publicBaseUrl + "/");
        }
        
        RegisteredClient spaClient = spaClientBuilder
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("read")
                .scope("write")
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true) // Require PKCE for public SPA client
                        .requireAuthorizationConsent(false)
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(gatewayClient, spaClient);
    }
}
