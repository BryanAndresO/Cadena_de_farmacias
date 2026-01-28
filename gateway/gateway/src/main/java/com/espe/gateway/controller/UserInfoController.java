package com.espe.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserInfoController {

    @Value("${OAUTH_PUBLIC_URL:http://localhost:9000}")
    private String oauthPublicUrl;

    @GetMapping("/api/userinfo")
    public Mono<ResponseEntity<Map<String, Object>>> getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return Mono.just(ResponseEntity.ok()
                    .header("Cache-Control", "no-store, no-cache, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .body(Map.of("authenticated", false)));
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("authenticated", true);
        userInfo.put("username",
                oidcUser.getPreferredUsername() != null ? oidcUser.getPreferredUsername() : oidcUser.getSubject());
        userInfo.put("name", oidcUser.getFullName() != null ? oidcUser.getFullName() : oidcUser.getSubject());
        userInfo.put("email", oidcUser.getEmail());
        userInfo.put("roles", oidcUser.getClaimAsStringList("roles"));

        return Mono.just(ResponseEntity.ok()
                .header("Cache-Control", "no-store, no-cache, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(userInfo));
    }

    @PostMapping("/api/logout")
    public Mono<ResponseEntity<Map<String, Object>>> logout(
            @AuthenticationPrincipal OidcUser oidcUser,
            ServerWebExchange exchange) {

        // Build the OAuth2 provider logout URL
        String logoutUrl;

        if (oidcUser != null && oidcUser.getIdToken() != null) {
            // Build full logout URL to oauth-server with id_token_hint
            String idToken = oidcUser.getIdToken().getTokenValue();
            String baseUrl = exchange.getRequest().getURI().getScheme() + "://" +
                    exchange.getRequest().getHeaders().getFirst("Host");
            String postLogoutRedirectUri = baseUrl + "/oauth2/authorization/gateway-client";

            logoutUrl = oauthPublicUrl + "/connect/logout" +
                    "?id_token_hint=" + URLEncoder.encode(idToken, StandardCharsets.UTF_8) +
                    "&post_logout_redirect_uri=" + URLEncoder.encode(postLogoutRedirectUri, StandardCharsets.UTF_8);
        } else {
            // No session, just redirect to login
            logoutUrl = "/oauth2/authorization/gateway-client";
        }

        // Invalidate the session
        return exchange.getSession()
                .flatMap(WebSession::invalidate)
                .then(Mono.just(ResponseEntity.ok()
                        .header("Cache-Control", "no-store, no-cache, must-revalidate")
                        .header("Pragma", "no-cache")
                        .header("Expires", "0")
                        .body(Map.of(
                                "success", true,
                                "logoutUrl", logoutUrl))));
    }
}
