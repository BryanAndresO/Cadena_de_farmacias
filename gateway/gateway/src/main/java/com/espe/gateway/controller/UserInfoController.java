package com.espe.gateway.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserInfoController {

    @GetMapping("/api/userinfo")
    public Mono<ResponseEntity<Map<String, Object>>> getUserInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return Mono.just(ResponseEntity.ok(Map.of("authenticated", false)));
        }
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("authenticated", true);
        userInfo.put("username", oidcUser.getPreferredUsername() != null ? oidcUser.getPreferredUsername() : oidcUser.getSubject());
        userInfo.put("name", oidcUser.getFullName() != null ? oidcUser.getFullName() : oidcUser.getSubject());
        userInfo.put("email", oidcUser.getEmail());
        userInfo.put("roles", oidcUser.getClaimAsStringList("roles"));
        
        return Mono.just(ResponseEntity.ok(userInfo));
    }

    @PostMapping("/api/logout")
    public Mono<ResponseEntity<Map<String, Object>>> logout() {
        // The actual logout will be handled by Spring Security's logout handler
        // This endpoint just returns a response to the frontend
        return Mono.just(ResponseEntity.ok(Map.of(
            "success", true,
            "logoutUrl", "/logout"
        )));
    }
}
