package com.exemple.authservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;


@Service
public class VerificationService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationService.class);
    private static final String EMAIL_VERIFICATION_PREFIX = "email_verify:";
    private static final String PASSWORD_RESET_PREFIX = "password_reset:";
    private static final int CODE_LENGTH = 6;
    private static final long CODE_EXPIRATION_MINUTES = 15;

    private final SecureRandom secureRandom ;
    private final RedisTemplate<String, String> redisTemplate;

    public VerificationService( RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.secureRandom = new SecureRandom();
    }

    // Génère un code de vérification de 6 chiffres
    public String generateVerificationCode() {
        int code = secureRandom.nextInt(900000) + 100000; // garantit un code à 6 chiffres
        logger.info("Generated verification code: {}", code);
        return String.valueOf(code);
    }

    // stocke le code email dans redis avec une expiration
    public void storeEmailVerificationCode(String email, String code) {
        String key = EMAIL_VERIFICATION_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRATION_MINUTES, java.util.concurrent.TimeUnit.MINUTES);
        logger.info("Stored email verification code for {}: {}", email, code);
    }

    // Verifier le code email
    public boolean verifyEmailCode(String email, String code) {
        String key = EMAIL_VERIFICATION_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);
        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(key); // Supprimer le code après vérification réussie
            logger.info("Email verification successful for {}: {}", email, code);
            return true;
        }
        logger.warn("Email verification failed for {}: {}", email, code);
        return false;
    }

    //stocke un code de  reset password
    public void storePasswordResetCode(String email, String code) {
        String key = PASSWORD_RESET_PREFIX + email;
        redisTemplate.opsForValue().set(key, code, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        logger.info("Stored password reset code for {}: {}", email, code);
    }

    // Verifier le code de reset password
    public boolean verifyPasswordResetCode(String email, String code) {
        String key = PASSWORD_RESET_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(key); // Supprimer le code après vérification réussie
            logger.info("Password reset verification successful for {}: {}", email, code);
            return true;
        }
        logger.warn("Password reset verification failed for {}: {}", email, code);
        return false;
    }


}
