package com.chatop.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    // Définit les règles CORS pour votre application
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();                                         // Sera utilisé pour définir les règles concernant les requêtes transfrontalières pour l'API.
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));                             // Définit les origines autorisées à accéder à votre serveur. ici 4200
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));// Spécifie les méthodes HTTP autorisées lors des requêtes cross-origin.
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));  //  Indique les en-têtes HTTP qui peuvent être utilisés pendant la requête.
        configuration.setAllowCredentials(true);                                                           // Permet les requêtes à inclure des informations d'identification telles que les cookies ou les en-têtes d'autorisation
        configuration.setMaxAge(3600L);                                                                    // 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();                    // C'est un objet qui stocke la configuration CORS
        source.registerCorsConfiguration("/**", configuration);                                     // associe la configuration CORS a un chemin spécifique, ici toutes
        return source;                                                                                      // renvoie l'objet UrlBasedCorsConfigurationSource avec les règles CORS .
                                                                                                            // Cet objet est utilisé par Spring pour appliquer la politique CORS à toutes les requêtes vers l'API.
    }
}
