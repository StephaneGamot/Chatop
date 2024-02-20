package com.chatop.backend.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

/*
 * ModelMapper est une bibliothèque qui permet de mapper automatiquement les attributs d'un objet à un autre,
 * Il est souvent utilisé pour transférer des données entre les objets de domaine (entités) et les Data Transfer Objects (DTOs).
 * Il permet à Spring de créer et de gérer une instance de ModelMapper qui pourra être injectée là où on veut dans l'application.
 * */