package com.exemple.authservice.model.response;

import lombok.Builder;

@Builder
public record AuthResponse(
         boolean success,
         String message,
         Object data
) {
}
