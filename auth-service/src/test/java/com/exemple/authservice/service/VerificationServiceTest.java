package com.exemple.authservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VerificationServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private VerificationService verificationService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        verificationService = new VerificationService(redisTemplate);
    }

    // ==================== generateVerificationCode ====================

    @Test
    @DisplayName("Doit générer un code à 6 chiffres")
    void shouldGenerateSixDigitCode() {
        // When
        String code = verificationService.generateVerificationCode();

        // Then
        assertThat(code).hasSize(6);
        assertThat(code).matches("\\d{6}"); // Que des chiffres
        assertThat(Integer.parseInt(code)).isBetween(100000, 999999);
    }

    @Test
    @DisplayName("Doit générer des codes différents à chaque appel")
    void shouldGenerateDifferentCodes() {
        // When
        String code1 = verificationService.generateVerificationCode();
        String code2 = verificationService.generateVerificationCode();
        String code3 = verificationService.generateVerificationCode();

        // Then - Au moins 2 codes sur 3 devraient être différents
        boolean allSame = code1.equals(code2) && code2.equals(code3);
        assertThat(allSame).isFalse();
    }

    // ==================== storeEmailVerificationCode ====================

    @Test
    @DisplayName("Doit stocker le code email dans Redis avec expiration de 15 minutes")
    void shouldStoreEmailVerificationCodeInRedis() {
        // Given
        String email = "test@example.com";
        String code = "123456";

        // When
        verificationService.storeEmailVerificationCode(email, code);

        // Then
        verify(valueOperations).set(
                eq("email_verify:test@example.com"),
                eq("123456"),
                eq(15L),
                eq(TimeUnit.MINUTES)
        );
    }

    // ==================== verifyEmailCode ====================

    @Test
    @DisplayName("Doit retourner true si le code email est valide")
    void shouldReturnTrueWhenEmailCodeIsValid() {
        // Given
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("email_verify:test@example.com")).thenReturn("123456");
        when(redisTemplate.delete("email_verify:test@example.com")).thenReturn(true);

        // When
        boolean result = verificationService.verifyEmailCode(email, code);

        // Then
        assertThat(result).isTrue();
        verify(redisTemplate).delete("email_verify:test@example.com");
    }

    @Test
    @DisplayName("Doit retourner false si le code email est invalide")
    void shouldReturnFalseWhenEmailCodeIsInvalid() {
        // Given
        String email = "test@example.com";
        String code = "wrong-code";
        when(valueOperations.get("email_verify:test@example.com")).thenReturn("123456");

        // When
        boolean result = verificationService.verifyEmailCode(email, code);

        // Then
        assertThat(result).isFalse();
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    @DisplayName("Doit retourner false si le code n'existe pas dans Redis")
    void shouldReturnFalseWhenCodeNotFound() {
        // Given
        String email = "test@example.com";
        String code = "123456";
        when(valueOperations.get("email_verify:test@example.com")).thenReturn(null);

        // When
        boolean result = verificationService.verifyEmailCode(email, code);

        // Then
        assertThat(result).isFalse();
    }

    // ==================== storePasswordResetCode ====================

    @Test
    @DisplayName("Doit stocker le code reset password dans Redis")
    void shouldStorePasswordResetCodeInRedis() {
        // Given
        String email = "test@example.com";
        String code = "654321";

        // When
        verificationService.storePasswordResetCode(email, code);

        // Then
        verify(valueOperations).set(
                eq("password_reset:test@example.com"),
                eq("654321"),
                eq(15L),
                eq(TimeUnit.MINUTES)
        );
    }

    // ==================== verifyPasswordResetCode ====================

    @Test
    @DisplayName("Doit valider le code reset password correct")
    void shouldValidateCorrectPasswordResetCode() {
        // Given
        String email = "test@example.com";
        String code = "654321";
        when(valueOperations.get("password_reset:test@example.com")).thenReturn("654321");
        when(redisTemplate.delete("password_reset:test@example.com")).thenReturn(true);

        // When
        boolean result = verificationService.verifyPasswordResetCode(email, code);

        // Then
        assertThat(result).isTrue();
        verify(redisTemplate).delete("password_reset:test@example.com");
    }

    @Test
    @DisplayName("Doit rejeter le code reset password incorrect")
    void shouldRejectIncorrectPasswordResetCode() {
        // Given
        String email = "test@example.com";
        String code = "wrong";
        when(valueOperations.get("password_reset:test@example.com")).thenReturn("654321");

        // When
        boolean result = verificationService.verifyPasswordResetCode(email, code);

        // Then
        assertThat(result).isFalse();
    }
}