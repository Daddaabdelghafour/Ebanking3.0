package com.exemple.authservice.service;


import com.exemple.authservice.client.KeycloakClient;
import com.exemple.authservice.exception.EmailNotVerifiedException;
import com.exemple.authservice.model.event.*;
import com.exemple.authservice.model.request.*;
import com.exemple.authservice.model.response.AuthResponse;
import com.exemple.authservice.model.response.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util. Map;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final KeycloakClient keycloakClient;
    private final VerificationService verificationService;
    private final KafkaProducerService kafkaProducerService;

    public AuthService(KeycloakClient keycloakClient, KafkaProducerService kafkaProducerService,
                       VerificationService verificationService) {

        this.verificationService = verificationService;
        this.kafkaProducerService = kafkaProducerService;
        this.keycloakClient = keycloakClient;
    }

    /**
     * Authentifie un utilisateur
     */
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.email());

        try {
            // verifief si email verified
            boolean isEmailVerified = keycloakClient.isEmailVerified(loginRequest.email());
           if (!isEmailVerified) {
                logger.warn("Login failed - email not verified for user: {}", loginRequest.email());
                throw new EmailNotVerifiedException(loginRequest.email());  // Just pass email
            }
            // Appeler Keycloak pour authentifier
            Map<String, Object> keycloakResponse = keycloakClient.authenticate(
                    loginRequest.email(),
                    loginRequest.password()
            );


            // Extraire les informations du token
            String accessToken = (String) keycloakResponse.get("access_token");
            String refreshToken = (String) keycloakResponse.get("refresh_token");
            Integer expiresIn = (Integer) keycloakResponse.get("expires_in");

            // Décoder le JWT pour extraire les informations utilisateur
            String keycloakUserId = extractUserIdFromToken(accessToken);
            String[] roles = extractRolesFromToken(accessToken);

            // Construire la réponse
            LoginResponse response = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(expiresIn)
                    .keycloakUserId(keycloakUserId)
                    .email(loginRequest.email())
                    .roles(roles)
                    .build();

            logger. info("Login successful for user: {}", loginRequest.email());
            return response;
        } catch (EmailNotVerifiedException e) {
            // Re-throw EmailNotVerifiedException directly so GlobalExceptionHandler can catch it
            throw e;
        } catch (Exception e) {
            logger.error("Login failed for user: {}", loginRequest.email(), e);
            throw new RuntimeException("Invalid email or password");
        }
    }

    /**
     * Enregistre un nouvel utilisateur
     */
    public AuthResponse register(RegisterRequest request) {
        logger.info("Registration attempt for user: {}", request.username());

        try {
            // Creer dans Keyloak
            String keycloakUserId = keycloakClient.createUser(
                    request.username(),
                    request.email(),
                    request.password(),
                    request.firstName(),
                    request.lastName()
            );

            logger.info("User created in Keycloak with ID: {}", keycloakUserId);

            // assigner un role par defaut
            keycloakClient.assignRole(keycloakUserId, "CUSTOMER");

            // Generer un code  de verification
            String verificationCode = verificationService.generateVerificationCode();
            // Stocker le code de verification
            verificationService.storeEmailVerificationCode(request.email(), verificationCode);

            // Publier un evenement NewRegistred a notif service
            NewRegistredEvent registredEvent = NewRegistredEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .email(request.email())
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .verificationCode(verificationCode)
                    .validityPeriod("15 minutes")
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaProducerService.sendNewRegistredEvent(registredEvent);


            // Publier un evenement UserRegistered a user service
            UserRegisteredEvent event = UserRegisteredEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .keycloakUserId(keycloakUserId)
                    .email(request.email())
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .phone(request.phone())
                    .address(request.address())
                    .city(request.city())
                    .country(request.country())
                    .profession(request.profession())
                    .gender(request.gender())
                    .dateOfBirth(request.dateOfBirth())
                    .nationality(request.nationality())
                    .cinOrPassport(request.cinOrPassport())
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaProducerService.sendUserRegisteredEvent(event);

            logger.info("Registration successful for user: {}", request.username());

            return AuthResponse.builder()
                    .success(true)
                    .message("User registered successfully. Please verify your email.")
                    .data(Map.of(
                            "keycloakUserId", keycloakUserId,
                            "email", request.email()
                    ))
                    .build();

        } catch (Exception e) {
            logger.error("Registration failed for user: {}", request.username());
            throw new RuntimeException("User registration failed: " + e.getMessage());
        }
    }

    // Recupere le profils utilisateur
    // a avoir est ce que le service user-service doit etre appele pour recuperer les infos supplementaires
    // il faut consommer un event kafka pour synchroniser les donnees supplementaires
    public AuthResponse getUserProfile(String username) {
        logger.info("Fetching user profile for: {}", username);

        //User user = userService.getUserByUsername(username);
        return null;
    }

    /**
     * Rafraîchit un token expiré
     */
    public LoginResponse refreshToken(String refreshToken) {
        logger.info("Token refresh attempt");

        try {
            Map<String, Object> keycloakResponse = keycloakClient.refreshToken(refreshToken);

            String newAccessToken = (String) keycloakResponse.get("access_token");
            String newRefreshToken = (String) keycloakResponse.get("refresh_token");
            Integer expiresIn = (Integer) keycloakResponse.get("expires_in");

            String[] roles = extractRolesFromToken(newAccessToken);

            LoginResponse response = LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(expiresIn)
                    .roles(roles)
                    .build();

            logger.info("Token refreshed successfully");
            return response;

        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            throw new RuntimeException("Token refresh failed:  " + e.getMessage());
        }
    }

    /**
     * Déconnecte l'utilisateur
     */
    public void logout(String refreshToken) {
        logger.info("Logout attempt");

        try {
            keycloakClient.logout(refreshToken);
            logger.info("Logout successful");

        } catch (Exception e) {
            logger. error("Logout failed", e);
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }

    // Verifier EMAIL - ecenement avec notif serive
    public AuthResponse verifyEmail(VerifyEmailRequest request) {
        logger.info("Email verification attempt for: {}", request.email());

        try {
            // Verifier le code
            boolean isValid = verificationService.verifyEmailCode(
                    request.email(),
                    request.verificationCode()
            );
            if (!isValid) {
                return AuthResponse.builder()
                        .success(false)
                        .message("Invalid verification code")
                        .build();
            }


            // Recuperer l'utilisateur dans Keycloak par email
            String keycloakUserId = keycloakClient.getUserIdByEmail(request.email());


            // Marquer l'email comme verifié dans Keycloak
            keycloakClient.setEmailVerified(keycloakUserId, true);


            // Envoyer un evenement a Kafka
            EmailVerificationEvent event = EmailVerificationEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .email(request.email())
                    .verified(true)
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaProducerService.sendEmailVerificationEvent(event);

            logger.info("Email verification successful for: {}", request.email());

            return AuthResponse.builder()
                    .success(true)
                    .message("Email verified successfully")
                    .data(Map.of(
                            "keycloakUserId", keycloakUserId,
                            "email", request.email()
                    ))
                    .build();
        } catch (Exception e) {
            logger.error("Email verification failed for: {}", request.email());
            throw new RuntimeException("Email verification failed: " + e.getMessage());
        }
    }

    // Resend verification code
    public AuthResponse resendVerificationCode(String email) {
        logger.info("Resend verification code attempt for: {}", email);

        try {
            // Generer un nouveau code de verification
            String verificationCode = verificationService.generateVerificationCode();
            // Stocker le code de verification
            verificationService.storeEmailVerificationCode(email, verificationCode);

            // Recuperer l'utilisateur dans Keycloak par email
            String keycloakUserId = keycloakClient.getUserIdByEmail(email);

            // Publier un evenement UserRegistered
            ResendVerificationCodeEvent event = ResendVerificationCodeEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .email(email)
                    .verificationCode(verificationCode)
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaProducerService.sendResendVerificationCodeEvent(event);

            logger.info("Resend verification code successful for: {}", email);

            return AuthResponse.builder()
                    .success(true)
                    .message("Verification code resent successfully.")
                    .data(Map.of(
                            "keycloakUserId", keycloakUserId,
                            "email", email
                    ))
                    .build();

        } catch (Exception e) {
            logger.error("Resend verification code failed for: {}", email);
            throw new RuntimeException("Resend verification code failed: " + e.getMessage());
        }
    }


    // Forget Password
    // il faut verifier que roles contient CUSTOMER pour envoyer l'evenement a user-service
    public AuthResponse forgetPassword(ForgetPasswordResquest resquest) {
        logger.info("Forget password attempt for: {}", resquest.email());

        try {
            // Verifier existance du user
            String keycloakUserId = keycloakClient.getUserIdByEmail(resquest.email());

            // Generer un code de reset password
            String resetCode = verificationService.generateVerificationCode();

            verificationService.storePasswordResetCode(resquest.email(), resetCode);

            // Publier un evenement a Kafka pour notif service
            ForgetPasswordEvent event = ForgetPasswordEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .email(resquest.email())
                    .resetCode(resetCode)
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaProducerService.sendForgetPasswordEvent(event);

            return AuthResponse.builder()
                    .success(true)
                    .message("Password reset code sent to email.")
                    .data(Map.of(
                            "keycloakUserId", keycloakUserId,
                            "email", resquest.email()
                    ))
                    .build();
        } catch (Exception e) {
            logger.error("Password reset code failed for: {}", resquest.email());
            throw new RuntimeException("Password reset request failed: " + e.getMessage());
        }
    }

    // Reset Password avec code
    public AuthResponse resetPassword(ResetPasswordResquest request) {
        logger.info("Reset password attempt for: {}", request.email());

        try {

            // Verifier le code de reset
            boolean isValid = verificationService.verifyPasswordResetCode(
                    request.email(),
                    request.code()
            );

            if (!isValid) {
                return AuthResponse.builder()
                        .success(false)
                        .message("Invalid password reset code")
                        .build();
            }

            // Recuperer l'utilisateur dans Keycloak par email
            String keycloakUserId = keycloakClient.getUserIdByEmail(request.email());

            // Mettre a jour le mot de passe dans Keycloak
            keycloakClient.resetPasswordUser(
                    keycloakUserId,
                    request.newPassword()
            );


//                // Publier un evenement a Kafka au user-service
//                PasswordResetEvent event = PasswordResetEvent.builder()
//                        .eventId(UUID.randomUUID().toString())
//                        .eventType("PASSWORD_RESET_COMPLETED")
//                        .keycloakUserId(keycloakUserId)
//                        .email(request.email())
//                        .timestamp(LocalDateTime.now())
//                        .build();
//                kafkaProducerService.sendPasswordResetEvent(event);


            return AuthResponse.builder()
                    .success(true)
                    .message("Password reset successfully.")
                    .data(Map.of(
                            "keycloakUserId", keycloakUserId,
                            "email", request.email()
                    ))
                    .build();

        } catch (Exception e) {
            logger.error("Password reset failed for: {}", request.email());
            throw new RuntimeException("Password reset failed: " + e.getMessage());
        }
    }

    // change password
    public AuthResponse changePassword(String keycloakUserId, ChangePasswordRequest  request) {
        logger.info("change password attempt for userId: {}", keycloakUserId);

        try {

            String email = keycloakClient.getUserEmail(keycloakUserId);

            // Verifier ancien mot de passe
            try {
                keycloakClient.authenticate(email, request.currentPassword());
            } catch (Exception e) {
                return AuthResponse.builder()
                        .success(false)
                        .message("Old password is incorrect.")
                        .build();
            }

            // Mettre a jour le mot de passe
            //keycloakClient.resetPassword(keycloakUserId, request.newPassword());
            keycloakClient.resetPasswordUser(keycloakUserId, request.newPassword());

            // user-service n a pas de password donc pas besoin d'envoyer un evenement
//            if (keycloakClient.getUserRoles(keycloakUserId)
//                    .contains("CUSTOMER"))
//            {
//                // Publier un evenement a Kafka au user-service
//                PasswordResetEvent event = PasswordResetEvent.builder()
//                        .eventId(UUID.randomUUID().toString())
//                        .eventType("PASSWORD_CHANGED")
//                        .keycloakUserId(keycloakUserId)
//                        .email(email)
//                        .timestamp(LocalDateTime.now())
//                        .build();
//                kafkaProducerService.sendPasswordResetEvent(event);
//            }

            logger.info("Password changed successfully for userId: {}", keycloakUserId);

            return AuthResponse.builder()
                    .success(true)
                    .message("Password changed successfully.")
                    .build();

        } catch (Exception e) {
            logger.error("Change password failed for userId: {}", keycloakUserId);
            throw new RuntimeException(e);
        }
    }


    // Update Email
    public AuthResponse updateEmail(String keycloakUserId, UpdateEmailRequest request) {
        logger.info("update email for userId: {}", keycloakUserId);

        try {

            // recuperer l'email actuel
            String currentEmail = keycloakClient.getUserEmail(keycloakUserId);

            // Verifier le mot de passe
            try {
                keycloakClient.authenticate(currentEmail, request.password());
            } catch (Exception e) {
                return AuthResponse.builder()
                        .success(false)
                        .message("Password is incorrect.")
                        .build();
            }
            // Verifier existance du new email
            if (keycloakClient.emailExists(request.newEmail())){
                return AuthResponse.builder()
                        .success(false)
                        .message("The new email is already in use.")
                        .build();
            }

            // Mettre a jour l'email dans Keycloak
            keycloakClient.updateEmail(keycloakUserId, request.newEmail());

            // Marquer l'email comme non verifié
            keycloakClient.setEmailVerified(keycloakUserId, false);

            // Generer un code  de verification
            String verificationCode = verificationService.generateVerificationCode();
            verificationService.storeEmailVerificationCode(request.newEmail(), verificationCode);

             //Publier un evenement UserRegistered pour user-service
            if (keycloakClient.getUserRoles(keycloakUserId)
                    .contains("CUSTOMER"))
            {
                EmailUpdatedEvent event = EmailUpdatedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .oldEmail(currentEmail)
                        .newEmail(request.newEmail())
                        .timestamp(LocalDateTime.now())
                        .build();

                kafkaProducerService.sendEmailUpdatedEvent(event);
            }

            return AuthResponse.builder()
                    .success(true)
                    .message("Email updated successfully. Please verify your new email.")
                    .data(Map.of(
                            "newEmail", request.newEmail()
                    ))
                    .build();

        } catch (Exception e) {
            logger.error("Email update failed for: {}", request.newEmail());
            throw new RuntimeException("Email update failed: " + e.getMessage());
        }
    }


    // Extraire le userId (sub) depuis le JWT token
    private String extractUserIdFromToken(String accessToken) {
        try {
            String[] parts = accessToken.split("\\.");

            if (parts.length < 2) {
                throw new RuntimeException("Invalid access token");
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

            // Parser le Json pour extraire sub
            int sybStart = payload.indexOf("\"sub\":\"") + 7;
            int subEnd = payload.indexOf("\"", sybStart);
            return payload.substring(sybStart, subEnd);

        } catch (Exception e) {
            logger.error("Failed to extract user ID from token", e);
            throw new RuntimeException("Invalid access token");
        }
    }

    // Extraire les roles du JWT token
    private String[] extractRolesFromToken(String accessToken) {
        try {

            String[] parts = accessToken.split("\\.");
            if (parts.length < 2) {
                return new String[0];
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

            if (payload.contains("realm_access") && payload.contains("roles")) {
                int rolesStart = payload.indexOf("\"roles\":[") + 9;
                int rolesEnd = payload.indexOf("]", rolesStart);
                String rolesStr = payload.substring(rolesStart, rolesEnd);

                rolesStr = rolesStr.replace("\"", "").trim();
                return rolesStr.split(",");
            }
            return new String[0];

        } catch (Exception e) {
            logger.info("Failed to extract roles from token", e);
            return  new String[0];
        }
    }
}
