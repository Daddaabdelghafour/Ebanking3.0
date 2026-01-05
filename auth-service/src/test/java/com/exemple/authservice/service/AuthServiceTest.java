package com.exemple.authservice.service;

import com.exemple.authservice.client.KeycloakClient;
import com.exemple.authservice.model.event.NewRegistredEvent;
import com.exemple.authservice.model.event.UserRegisteredEvent;
import com.exemple.authservice.model.request.*;
import com.exemple.authservice.model.response.AuthResponse;
import com.exemple.authservice.model.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private KeycloakClient keycloakClient;

    @Mock
    private VerificationService verificationService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(keycloakClient, kafkaProducerService, verificationService);
    }

    // ==================== LOGIN ====================

    @Test
    @DisplayName("Doit retourner LoginResponse quand login réussi")
    void shouldReturnLoginResponseWhenLoginSuccessful() {
        // Given
        LoginRequest request = new LoginRequest("user@example.com", "password123");

        // Token JWT simulé (format simplifié pour le test)
        String fakeJwt = createFakeJwt("user-123", new String[]{"CUSTOMER"});

        Map<String, Object> keycloakResponse = Map.of(
                "access_token", fakeJwt,
                "refresh_token", "refresh-token-123",
                "expires_in", 3600
        );

        when(keycloakClient.authenticate("user@example.com", "password123"))
                .thenReturn(keycloakResponse);

        // Simuler que l'email est déjà vérifié
        verificationService.markEmailVerified("user@example.com");

        // When
        LoginResponse response = authService.login(request);

        // Then
        assertThat(response.accessToken()).isEqualTo(fakeJwt);
        assertThat(response.refreshToken()).isEqualTo("refresh-token-123");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600);
        assertThat(response.email()).isEqualTo("user@example.com");

        verify(keycloakClient).authenticate("user@example.com", "password123");
    }

    @Test
    @DisplayName("Doit lever exception quand login échoue")
    void shouldThrowExceptionWhenLoginFails() {
        // Given
        LoginRequest request = new LoginRequest("user@example.com", "wrong-password");

        when(keycloakClient.authenticate("user@example.com", "wrong-password"))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(com.exemple.authservice.exception.EmailNotVerifiedException.class)
                .hasMessageContaining("Email not verified");
    }

    // ==================== REGISTER ====================

    @Test
    @DisplayName("Doit enregistrer un utilisateur et envoyer les événements Kafka")
    void shouldRegisterUserAndSendKafkaEvents() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .password("Password123!")
                .firstName("John")
                .lastName("Doe")
                .phone("+33612345678")
                .address("123 Street")
                .city("Paris")
                .country("France")
                .profession("Engineer")
                .gender("M")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .nationality("French")
                .cinOrPassport("AB123456")
                .build();

        when(keycloakClient.createUser(
                "newuser", "new@example.com", "Password123!", "John", "Doe"
        )).thenReturn("kc-user-123");

        when(verificationService.generateVerificationCode()).thenReturn("123456");

        // When
        AuthResponse response = authService.register(request);

        // Then
        assertThat(response.success()).isTrue();
        assertThat(response.message()).contains("registered successfully");

        // Vérifier que le rôle CUSTOMER est assigné
        verify(keycloakClient).assignRole("kc-user-123", "CUSTOMER");

        // Vérifier que le code de vérification est stocké
        verify(verificationService).storeEmailVerificationCode("new@example.com", "123456");

        // Vérifier l'envoi de NewRegistredEvent (notification)
        ArgumentCaptor<NewRegistredEvent> notifEventCaptor = ArgumentCaptor.forClass(NewRegistredEvent.class);
        verify(kafkaProducerService).sendNewRegistredEvent(notifEventCaptor.capture());
        assertThat(notifEventCaptor.getValue().email()).isEqualTo("new@example.com");
        assertThat(notifEventCaptor.getValue().verificationCode()).isEqualTo("123456");

        // Vérifier l'envoi de UserRegisteredEvent (user-service)
        ArgumentCaptor<UserRegisteredEvent> userEventCaptor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(kafkaProducerService).sendUserRegisteredEvent(userEventCaptor.capture());
        assertThat(userEventCaptor.getValue().keycloakUserId()).isEqualTo("kc-user-123");
        assertThat(userEventCaptor.getValue().email()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Doit lever exception quand création utilisateur échoue")
    void shouldThrowExceptionWhenUserCreationFails() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .username("existinguser")
                .email("existing@example.com")
                .password("Password123!")
                .firstName("John")
                .lastName("Doe")
                .cinOrPassport("AB123456")
                .nationality("French")
                .gender("M")
                .phone("+33612345678")
                .build();

        when(keycloakClient.createUser(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("User already exists"));

        // When & Then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User registration failed");
    }

    // ==================== VERIFY EMAIL ====================

    @Test
    @DisplayName("Doit vérifier l'email avec un code valide")
    void shouldVerifyEmailWithValidCode() {
        // Given
        VerifyEmailRequest request = new VerifyEmailRequest("user@example.com", "123456");

        when(verificationService.verifyEmailCode("user@example.com", "123456")).thenReturn(true);
        when(keycloakClient.getUserIdByEmail("user@example.com")).thenReturn("kc-user-123");

        // When
        AuthResponse response = authService.verifyEmail(request);

        // Then
        assertThat(response.success()).isTrue();
        assertThat(response.message()).contains("verified successfully");

        verify(kafkaProducerService).sendEmailVerificationEvent(any());
    }

    @Test
    @DisplayName("Doit retourner erreur avec code de vérification invalide")
    void shouldReturnErrorWithInvalidVerificationCode() {
        // Given
        VerifyEmailRequest request = new VerifyEmailRequest("user@example.com", "wrong-code");

        when(verificationService.verifyEmailCode("user@example.com", "wrong-code")).thenReturn(false);

        // When
        AuthResponse response = authService.verifyEmail(request);

        // Then
        assertThat(response.success()).isFalse();
        assertThat(response.message()).contains("Invalid verification code");

        verify(kafkaProducerService, never()).sendEmailVerificationEvent(any());
    }

    // ==================== REFRESH TOKEN ====================

    @Test
    @DisplayName("Doit rafraîchir le token avec succès")
    void shouldRefreshTokenSuccessfully() {
        // Given
        String refreshToken = "old-refresh-token";
        String newFakeJwt = createFakeJwt("user-123", new String[]{"CUSTOMER"});

        Map<String, Object> keycloakResponse = Map.of(
                "access_token", newFakeJwt,
                "refresh_token", "new-refresh-token",
                "expires_in", 3600
        );

        when(keycloakClient.refreshToken(refreshToken)).thenReturn(keycloakResponse);

        // When
        LoginResponse response = authService.refreshToken(refreshToken);

        // Then
        assertThat(response.accessToken()).isEqualTo(newFakeJwt);
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
    }

    // ==================== LOGOUT ====================

    @Test
    @DisplayName("Doit déconnecter l'utilisateur avec succès")
    void shouldLogoutSuccessfully() {
        // Given
        String refreshToken = "refresh-token";
        doNothing().when(keycloakClient).logout(refreshToken);

        // When
        authService.logout(refreshToken);

        // Then
        verify(keycloakClient).logout(refreshToken);
    }

    // ==================== FORGET PASSWORD ====================

    @Test
    @DisplayName("Doit envoyer le code de reset password")
    void shouldSendPasswordResetCode() {
        // Given
        ForgetPasswordResquest request = new ForgetPasswordResquest("user@example.com");

        when(keycloakClient.getUserIdByEmail("user@example.com")).thenReturn("kc-user-123");
        when(verificationService.generateVerificationCode()).thenReturn("654321");

        // When
        AuthResponse response = authService.forgetPassword(request);

        // Then
        assertThat(response.success()).isTrue();
        assertThat(response.message()).contains("reset code sent");

        verify(verificationService).storePasswordResetCode("user@example.com", "654321");
        verify(kafkaProducerService).sendForgetPasswordEvent(any());
    }

    // ==================== RESET PASSWORD ====================

    @Test
    @DisplayName("Doit réinitialiser le mot de passe avec code valide")
    void shouldResetPasswordWithValidCode() {
        // Given
        ResetPasswordResquest request = new ResetPasswordResquest(
                "user@example.com", "654321", "NewPassword123!"
        );

        when(verificationService.verifyPasswordResetCode("user@example.com", "654321")).thenReturn(true);
        when(keycloakClient.getUserIdByEmail("user@example.com")).thenReturn("kc-user-123");
        doNothing().when(keycloakClient).resetPasswordUser("kc-user-123", "NewPassword123!");

        // When
        AuthResponse response = authService.resetPassword(request);

        // Then
        assertThat(response.success()).isTrue();
        assertThat(response.message()).contains("reset successfully");

        verify(keycloakClient).resetPasswordUser("kc-user-123", "NewPassword123!");
    }

    @Test
    @DisplayName("Doit refuser reset password avec code invalide")
    void shouldRejectResetPasswordWithInvalidCode() {
        // Given
        ResetPasswordResquest request = new ResetPasswordResquest(
                "user@example.com", "wrong-code", "NewPassword123!"
        );

        when(verificationService.verifyPasswordResetCode("user@example.com", "wrong-code")).thenReturn(false);

        // When
        AuthResponse response = authService.resetPassword(request);

        // Then
        assertThat(response.success()).isFalse();
        assertThat(response.message()).contains("Invalid password reset code");

        verify(keycloakClient, never()).resetPasswordUser(any(), any());
    }

    // ==================== HELPER ====================

    /**
     * Crée un faux JWT pour les tests (format simplifié)
     */
    private String createFakeJwt(String userId, String[] roles) {
        // Header: {"alg":"RS256","typ":"JWT"}
        String header = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9";

        // Payload simulé avec sub et realm_access
        String payload = java.util.Base64.getUrlEncoder().encodeToString(
                String.format(
                        "{\"sub\":\"%s\",\"realm_access\":{\"roles\":[\"%s\"]}}",
                        userId,
                        String.join("\",\"", roles)
                ).getBytes()
        );

        String signature = "fake-signature";

        return header + "." + payload + "." + signature;
    }
}