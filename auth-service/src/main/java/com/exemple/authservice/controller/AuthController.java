package com.exemple.authservice.controller;


import com.exemple.authservice.exception.EmailNotVerifiedException;
import com.exemple.authservice.model.request.*;
import com.exemple.authservice.model.response.AuthResponse;
import com.exemple.authservice.model.response.LoginResponse;
import com. exemple.authservice.service. AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /auth/login
     * Authentifie un utilisateur
     */
    @PostMapping("/public/login")
    public ResponseEntity<? > login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login request received for user: {}", request.email());

        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.
                    status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Login failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            AuthResponse.builder()
                                    .success(false)
                                    .message(e.getMessage())
                                    .build()
                    );
        }
    }

    /**
     * POST /auth/register
     * Enregistre un nouvel utilisateur
     */
    @PostMapping("/public/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration request received for user: {}", request.username());

        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception e) {
            logger.error("Registration failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder()
                    .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }


    /**
     * GET /auth/profile/{username}
     * Récupère le profil d'un utilisateur
     */
//    @GetMapping("/profile/{username}")
//    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
//        logger.info("Profile request for user: {}", username);
//
//        try {
//            UserDTO userDTO = authService.getUserProfile(username);
//            return ResponseEntity.ok(userDTO);
//
//        } catch (Exception e) {
//            logger.error("Failed to fetch profile", e);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of(
//                            "error", "User not found",
//                            "message", e.getMessage()
//                    ));
//        }
//    }


    /**
     * POST /auth/refresh
     * Rafraîchit un token expiré
     */
    @PostMapping("/public/refresh-token")
    public ResponseEntity<? > refreshToken(@RequestBody Map<String, String> request) {
        logger.info("Token refresh request received");

        String refreshToken = request.get("refresh_token");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false , "message","Refresh token is required"));
        }

        try {
            LoginResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }

    /**
     * POST /auth/logout
     * Déconnecte l'utilisateur
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        logger.info("Logout request received");

        String refreshToken = request.get("refresh_token");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false ,"message", "Refresh token is required"));
        }

        try {
            authService.logout(refreshToken);
            return ResponseEntity. ok(Map.of("success", true, "message", "Logout successful"));

        } catch (Exception e) {
            logger.error("Logout failed", e);
            return ResponseEntity. status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", e. getMessage()
                    ));
        }
    }

    /**
     * POST /auth/verify-email
     * Vérifie l'email avec le code
     */

    @PostMapping("/public/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        logger.info("Verify email request received for: {}", request.email());

        try{
            AuthResponse response = authService.verifyEmail(request);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            logger.error("Email verification failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder()
                    .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * POST /auth/resend-verification
     * Vérifie l'email avec le code
     */
    @PostMapping("/public/resend-verification-code")
    public ResponseEntity<AuthResponse> resendVerification(@RequestBody Map<String, String>  request) {

        String email = request.get("email");
        logger.info("Resend verification request for: {}", email);

        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("Email is required")
                            .build()
                    );
        }

        try {
            AuthResponse response = authService.resendVerificationCode(email);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            logger.error("Resend verification failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * POST /auth/forgot-password
     * Demande un code de reset password
     */
    @PostMapping("/public/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@Valid @RequestBody ForgetPasswordResquest resquest) {
        logger.info("Forgot password request for: {}", resquest.email());

        try {
            AuthResponse response = authService.forgetPassword(resquest);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            logger.error("Forgot password failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * POST /auth/reset-password
     * Reset le password avec le code
     */
    @PostMapping("/public/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody ResetPasswordResquest resquest) {
        logger.info("Reset password request for: {}", resquest.email());

        try {
            AuthResponse response = authService.resetPassword(resquest);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            logger.error("Reset password failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * POST /auth/change-password
     * Change le password (utilisateur connecté)
     * Nécessite JWT
     */
    @PostMapping("/change-password")
    public ResponseEntity<AuthResponse> changePassword(@AuthenticationPrincipal Jwt jwt
            ,@Valid @RequestBody ChangePasswordRequest request) {
        logger.info("changer password request");

        try {

            String keycloakUserId = jwt.getClaimAsString("sub");
            AuthResponse response = authService.changePassword(keycloakUserId, request);
            return ResponseEntity.ok(response);

        }catch (Exception e){
            logger.error("change password failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * POST /auth/update-email
     * Met à jour l'email (utilisateur connecté)
     * Nécessite JWT
     */
    @PostMapping("/update-email")
    public ResponseEntity<AuthResponse> updateEmail(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateEmailRequest request) {

        logger.info("update email request received");
        try {

            String keycloakUserId = jwt.getClaimAsString("sub");
            AuthResponse response = authService.updateEmail(keycloakUserId, request);
            return ResponseEntity.ok(response);

        }catch (Exception e){
            logger.error("update email failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build()
            );
        }

    }

    /**
     * GET /auth/me
     * Récupère les infos de l'utilisateur connecté depuis le JWT
     * Nécessite JWT
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        logger.info("Get current user request received");

        try {

            String keycloakUserId = jwt.getClaimAsString("sub");
            String email = jwt.getClaimAsString("email");
            String preferredUsername = jwt.getClaimAsString("preferred_username");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "id", keycloakUserId,
                            "email", email != null ? email : preferredUsername,
                            "claims", jwt.getClaims()
                    )
            ));

        } catch (Exception e) {
            logger.error("Get current user failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "Invalid toke"
                    ));
        }

    }


                                                       /**
     * GET /auth/health
     * Health check endpoint
     */@GetMapping("/public/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "auth-service",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
