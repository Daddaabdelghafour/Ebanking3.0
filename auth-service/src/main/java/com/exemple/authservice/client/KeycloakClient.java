package com.exemple.authservice.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework. http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class KeycloakClient {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakClient.class);

    private final WebClient webClient;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    public KeycloakClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Authentifie un utilisateur et récupère le JWT token
     */
    public Map<String, Object> authenticate(String username, String password) {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                keycloakServerUrl, realm);

        logger.info("Authenticating user: {} at {}", username, tokenUrl);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", username);
        formData.add("password", password);

        try {
            Map<String, Object> response = webClient.post()
                    .uri(tokenUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            logger.info("Authentication successful for user: {}", username);
            return response;

        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", username, e);
            throw new RuntimeException("Authentication failed:  " + e.getMessage());
        }
    }

    /**
     * Rafraîchit un token expiré
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                keycloakServerUrl, realm);

        logger.info("Refreshing token");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        try {
            Map<String, Object> response = webClient.post()
                    .uri(tokenUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            logger.info("Token refreshed successfully");
            return response;

        } catch (Exception e) {
            logger. error("Token refresh failed", e);
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }

    /**
     * Enregistre un nouvel utilisateur dans Keycloak
     */
    public String createUser(String username,String email, String password, String firstName, String lastName) {
        logger.info("Registering new user: {}", username);

        // Étape 1: Obtenir un token admin
        String adminToken = getAdminToken();

        // Étape 2: Créer l'utilisateur
        String createUserUrl = String.format("%s/admin/realms/%s/users",
                keycloakServerUrl, realm);

        Map<String, Object> userData = Map.of(
                "username", username,
                "email", email,
                "firstName", firstName,
                "lastName", lastName,
                "enabled", true,
                "emailVerified", false,
                "credentials", new Object[]{
                        Map.of(
                                "type", "password",
                                "value", password,
                                "temporary", false
                        )
                }
        );

        try {
            webClient.post()
                    .uri(createUserUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(userData)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            // Recupere ID de l utilisateur créé
            String userId = getUserIdByEmail(email);

            logger.info("User created successfully {} with ID : {}", username, userId);

            // Étape 3: Assigner le rôle USER par défaut
            //assignUserRole(username, adminToken);
            return userId;

        } catch (Exception e) {
            logger.error("User registration failed for: {}", username, e);
            throw new RuntimeException("User registration failed: " + e.getMessage());
        }
    }

    /**
     * resey ou Change le mot de passe d'un utilisateur (utilisateur connecté)
     */
    public void resetPasswordUser(String userId, String newPassword){
        logger.info("Resetting password for user ID: {}",userId);

        String adminToken = getAdminToken();
        String resetPasswordUrl = String.format("%s/admin/realms/%s/users/%s/reset-password",
                keycloakServerUrl, realm, userId);

        Map<String, Object> passwordData = Map.of(
                "type", "password",
                "value", newPassword,
                "temporary", false
        );

        try {
            webClient.put()
                    .uri(resetPasswordUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(passwordData)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            logger.info("Password reset successfully for user ID: {}", userId);

        } catch (Exception e) {
            logger.error("Password reset failed for user ID: {}", userId, e);
            throw new RuntimeException("Password reset failed: " + e.getMessage());
        }
    }


    /**
     * 🔁 Reset - (ADMIN ONLY – moins recommandé)
     */

    public Mono<Void> resetPassword(String userId, String newPassword) {

        String adminToken = getAdminToken();

        String resetPasswordUrl = String.format("%s/admin/realms/%s/users/%s/reset-password",
                keycloakServerUrl, realm, userId);

        Map<String, Object> credentialData = Map.of(
                "type", "password",
                "value", newPassword,
                "temporary", false
        );

        return webClient.put()
                .uri(resetPasswordUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(credentialData)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> logger.info("Password reset successfully for user ID: {}", userId))
                .doOnError(e -> logger.error("Password reset failed for user ID: {}", userId, e));
    }



    /**
     * Obtient un token admin pour effectuer des opérations administratives
     */
    private String getAdminToken() {
        String tokenUrl = String.format("%s/realms/master/protocol/openid-connect/token",
                keycloakServerUrl);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", "admin-cli");
        formData.add("username", adminUsername);
        formData.add("password", adminPassword);

        try {
            Map<String, Object> response = webClient.post()
                    .uri(tokenUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(Map. class)
                    .block();

            return (String) response.get("access_token");

        } catch (Exception e) {
            logger.error("Failed to get admin token", e);
            throw new RuntimeException("Failed to get admin token: " + e.getMessage());
        }
    }

    /**
     * Déconnecte l'utilisateur (logout)
     */
    public void logout(String refreshToken) {
        String logoutUrl = String.format("%s/realms/%s/protocol/openid-connect/logout",
                keycloakServerUrl, realm);

        logger.info("Logging out user");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        try {
            webClient.post()
                    .uri(logoutUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType. APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            logger.info("User logged out successfully");

        } catch (Exception e) {
            logger.error("Logout failed", e);
            throw new RuntimeException("Logout failed:  " + e.getMessage());
        }
    }

    /**
     * Assigne le rôle USER à un utilisateur nouvellement créé
     */
    public void assignRole(String userId, String roleName){
        logger.info("Assigning role {} to user ID: {}", roleName, userId);

        String adminToken = getAdminToken();
        String getRoleUrl = String.format("%s/admin/realms/%s/roles/%s",
                keycloakServerUrl, realm, roleName);

        Map<String, Object> roleData = webClient.get()
                .uri(getRoleUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String assignRoleUrl = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm",
                keycloakServerUrl, realm, userId);

        try {
            webClient.post()
                    .uri(assignRoleUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(List.of(roleData))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            logger.info("Role {} assigned to user ID: {}", roleName, userId);

        } catch (Exception e) {
            logger.error("Failed to assign role {} to user ID: {}", roleName, userId, e);
            throw new RuntimeException("Failed to assign role: " + e.getMessage());
        }
    }

    // Verifier si un mail existe deja dans keycloak
    public boolean emailExists(String email) {
        try{
            getUserIdByEmail(email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Recupere le userUd par email

    public String getUserIdByEmail(String email) {
        logger.info("Fetching user ID by email: {}", email);

        String adminToken = getAdminToken();
        String getUserUrl = String.format("%s/admin/realms/%s/users?email=%s",
                keycloakServerUrl, realm, email);

        try{
            List<Map<String, Object>> users = webClient.get()
                    .uri(getUserUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (users == null || users.isEmpty()) {
                throw new RuntimeException("User not found with email: " + email);
            }

            return (String) users.get(0).get("id");

        } catch (Exception e) {
            logger.info("Failed to fetch user ID by email: {}", email, e);
            throw new RuntimeException(e);
        }
    }


    // Recupere email dun user par son Id
    public String getUserEmail(String userId) {
        logger.info("Fetching user email by ID: {}", userId);

        String adminToken = getAdminToken();
        String getUserUrl = String.format("%s/admin/realms/%s/users/%s",
                keycloakServerUrl, realm, userId);

        try{
            Map<String, Object> user = webClient.get()
                    .uri(getUserUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (user == null) {
                throw new RuntimeException("User not found with ID: " + userId);
            }

            return (String) user.get("email");

        } catch (Exception e) {
            logger.info("Failed to fetch user email by ID: {}", userId, e);
            throw new RuntimeException(e);
        }
    }

    // verifie le mail dun user si non verifié
    public boolean isEmailVerified(String email) {
        logger.info("Checking if email is verified for user : {}", email);

        String userId = getUserIdByEmail(email);

        String adminToken = getAdminToken();
        String getUserUrl = String.format("%s/admin/realms/%s/users/%s",
                keycloakServerUrl, realm, userId);

        try{
            Map<String, Object> user = webClient.get()
                    .uri(getUserUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (user == null) {
                throw new RuntimeException("User not found with ID: " + userId);
            }

            return Boolean.TRUE.equals(user.get("emailVerified"));

        } catch (Exception e) {
            logger.info("Failed to check email verified status for user ID: {}", userId, e);
            throw new RuntimeException(e);
        }
    }

    // Marquer email comme verifié
    public void verifyEmail(String userId) {
        logger.info("Verifying email for user ID: {}", userId);

        String adminToken = getAdminToken();
        String updateUserUrl = String.format("%s/admin/realms/%s/users/%s",
                keycloakServerUrl, realm, userId);

        try{
            Map<String, Object> updateData = Map.of(
                    "emailVerified", true
            );

            webClient.put()
                    .uri(updateUserUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(updateData)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            logger.info("Email verified for user ID: {}", userId);

        } catch (Exception e) {
            logger.info("Failed to verify email for user ID: {}", userId, e);
            throw new RuntimeException(e);
        }
    }


    // Met a jour email dun user
    public void updateEmail(String userId, String newEmail) {
        logger.info("Updating email for user ID: {}", userId);

        String adminToken = getAdminToken();
        String updateUserUrl = String.format("%s/admin/realms/%s/users/%s",
                keycloakServerUrl, realm, userId);

        try{
            Map<String, Object> updateData = Map.of(
                    "email", newEmail
            );

            webClient.put()
                    .uri(updateUserUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(updateData)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            logger.info("Email updated for user ID: {}", userId);

        } catch (Exception e) {
            logger.info("Failed to update email for user ID: {}", userId, e);
            throw new RuntimeException(e);
        }
    }


    // definit le status de verification du mail
    public void setEmailVerified(String userId, boolean verified) {
        logger.info("Setting email verified status for user ID: {} to {}", userId, verified);

        String adminToken = getAdminToken();
        String updateUserUrl = String.format("%s/admin/realms/%s/users/%s",
                keycloakServerUrl, realm, userId);

        try{

            Map<String, Object> updateData = Map.of(
                    "emailVerified", verified
            );

            webClient.put()
                    .uri(updateUserUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(updateData)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            logger.info("Email verified status set for user ID: {} to {}", userId, verified);

        } catch (Exception e) {
            logger.info("Failed to set email verified status for user ID: {}", userId, e);
            throw new RuntimeException(e);
        }
    }


    // get Roles of user by userId
    public List<Map<String, Object>> getUserRoles(String userId) {
        logger.info("Fetching roles for user ID: {}", userId);
        String adminToken = getAdminToken();
        String getUserRolesUrl = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm",
                keycloakServerUrl, realm, userId);
        try {
            List<Map<String, Object>> roles = webClient.get()
                    .uri(getUserRolesUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();
            logger.info("Roles fetched for user ID: {}", userId);
            return roles;
        } catch (Exception e) {
            logger.info("Failed to fetch roles for user ID: {}", userId, e);
            throw new RuntimeException("Failed to fetch user roles: " + e.getMessage());
        }

    }



}
