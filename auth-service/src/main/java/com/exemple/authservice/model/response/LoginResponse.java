package com.exemple.authservice.model.response;

import lombok.Builder;

@Builder
public record LoginResponse(
         String accessToken,
         String refreshToken,
         String tokenType,
         int expiresIn,
         String keycloakUserId,
         String email,
         String[] roles
) {
}
