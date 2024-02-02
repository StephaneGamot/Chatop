package com.chatop.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Autoriser les requêtes de toutes les routes
                .allowedOrigins("http://localhost:4200") // URL de votre frontend Angular
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type")
          //      .exposedHeaders("Custom-Header1", "Custom-Header2")
                .maxAge(3600) // Par exemple, 1 heure
                .allowCredentials(true);
    }
}
