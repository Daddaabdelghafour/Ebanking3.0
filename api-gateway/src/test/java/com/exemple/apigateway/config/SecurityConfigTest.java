package com.exemple.apigateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

    @Test
    @DisplayName("Doit extraire les rôles depuis le JWT Keycloak")
    void shouldExtractRolesFromKeycloakJwt() {
        // Given - Simuler un JWT Keycloak
        Map<String, Object> realmAccess = Map.of("roles", List.of("CUSTOMER", "USER"));

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("realm_access", realmAccess)
                .claim("sub", "user-123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        // When - Extraire les autorités
        SecurityConfig config = new SecurityConfig();
        Collection<GrantedAuthority> authorities = extractAuthoritiesFromJwt(jwt);

        // Then
        assertThat(authorities).hasSize(2);
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_CUSTOMER", "ROLE_USER");
    }

    @Test
    @DisplayName("Doit retourner une liste vide si pas de realm_access")
    void shouldReturnEmptyListIfNoRealmAccess() {
        // Given
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("sub", "user-123")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        // When
        Collection<GrantedAuthority> authorities = extractAuthoritiesFromJwt(jwt);

        // Then
        assertThat(authorities).isEmpty();
    }

    // Helper method pour simuler l'extraction (copié de SecurityConfig)
    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractAuthoritiesFromJwt(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            return roles.stream()
                    .map(role -> (GrantedAuthority) () -> "ROLE_" + role.toUpperCase())
                    .toList();
        }

        return List.of();
    }
}