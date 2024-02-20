package com.chatop.backend.service.serviceImpl;

import com.chatop.backend.model.User;
import com.chatop.backend.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


//  Il permet à Spring Security d' obtenir les informations d'identification de l'utilisateur et il s'assure qu'elles sont enregistré.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Utilisation de UserRepository pour accéder aux données des utilisateurs dans la BDD.
    private final UserRepository userRepository;

    public CustomUserDetailsService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Méthode pour charger un utilisateur par son nom d'utilisateur (dans ce cas, son email).
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Recherche d'un utilisateur par email dans la base de données.
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));


        // Si trouvé alors UserDetails est créé et envoyé, il sera utilisé par Spring Security pour nous informer qu'il est un utilisateur authentifié.
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
