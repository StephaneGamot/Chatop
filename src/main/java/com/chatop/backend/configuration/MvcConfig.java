package com.chatop.backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc                                           // Active la configuration par défaut de Spring MVC
public class MvcConfig implements WebMvcConfigurer {    // Permet de personnaliser la configuration MVC de Spring :

    @Value("${app.upload.dir}")
    private String uploadDir;                           // Elle spécifie le répertoire où les fichiers sont téléchargés sur le serveur.

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Handler for images
        registry.addResourceHandler("/images/**")     // Elles seront servies à partir du dossier spécifié par uploadDir
                .addResourceLocations("file:" + uploadDir + "/");

        // Handler for Swagger UI
        registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/").resourceChain(false);               // désactive la mise en cache des ressources dans ce cas, ce qui signifie que les ressources sont toujours récupérées et non mises en cache pour les performances.
    }
}
