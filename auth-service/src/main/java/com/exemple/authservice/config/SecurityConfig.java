package com.exemple.authservice.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework. security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web. SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics (pas d'authentification requise)
                        .requestMatchers(
                                "/api/v1/auth/public/**",
                                "/actuator/**"
                        ).permitAll()

                        // Endpoints protégés (nécessitent JWT)
                        .requestMatchers(
                                "/api/v1/auth/**"
                        ).authenticated()

                        . requestMatchers("/actuator/**").permitAll()
                        // Toute autre requête nécessite une authentification
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer( oauth2 -> oauth2
                        .jwt(
                                jwt -> jwt.decoder(jwtDecoder())
                        )

                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(
                "http://localhost:8080/realms/e-banking/protocol/openid-connect/certs"
        ).build();
    }
}