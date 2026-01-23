package com.espe.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange(exchanges -> exchanges
                // Allow OAuth2 authorization endpoints and discovery to be proxied through the gateway
                .pathMatchers("/oauth2/**", "/.well-known/**", "/login/**", "/login/oauth2/**", "/favicon.ico").permitAll()
                // Allow frontend assets
                .pathMatchers("/assets/**", "/static/**").permitAll()
                // Everything else requires authentication
                .anyExchange().authenticated())
            .oauth2Login(Customizer.withDefaults())
            .oauth2Client(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable()); // Necesario para APIs REST

        return http.build();
        }
}
