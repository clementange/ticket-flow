package com.kfokam.ticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Couche : Configuration.
 * Responsabilité : autoriser le frontend React (http://localhost:5173)
 * à appeler le backend Spring Boot (http://localhost:8080).
 *
 * Sans cette configuration, le navigateur bloquerait toutes les requêtes
 * cross-origin (frontend et backend sur des ports différents = origines différentes).
 *
 * Source officielle : https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-cors
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Autorise uniquement le frontend React en développement
        config.addAllowedOrigin("http://localhost:5173");

        // Méthodes HTTP autorisées
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PATCH");
        config.addAllowedMethod("OPTIONS"); // Nécessaire pour les requêtes preflight

        // En-têtes autorisés (Content-Type pour le JSON)
        config.addAllowedHeader("*");

        // Applique cette configuration à tous les endpoints /api/**
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
