package com.chatop.backend.controller;

import com.chatop.backend.dto.UserDto;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.UserRepository;
import com.chatop.backend.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.chatop.backend.service.JWTService;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JWTService jwtService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public AuthController(JWTService jwtService, UserService userService, ModelMapper modelMapper) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Inscription d'un nouvel utilisateur", description = "Crée un nouvel utilisateur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Problème avec la requète 'création d'un utilisateur'"),
            @ApiResponse(responseCode = "409", description = "Nom d'utilisateur déjà utilisé"),
            @ApiResponse(responseCode = "500", description = "Erreur provenant du serveur")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.findByEmail(user.getName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nom d'utilisateur déjà utilisé");
        }
        UserDto savedUser = userService.registerUser(user);
        String token = jwtService.generateTokenForUser(savedUser);

        return ResponseEntity.ok().body(token);
    }

    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Non autorisé - Échec de l'authentification"),
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'a pas été retrouvé")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok(jwtService.generateToken(authentication));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Obtention des informations de l'utilisateur", description = "Retourne les informations de l'utilisateur actuellement authentifié")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations de l'utilisateur récupérées avec succès"),
            @ApiResponse(responseCode = "401", description = "Non autorisé - Utilisateur non authentifié"),
            @ApiResponse(responseCode = "404", description = "Non trouvé - Utilisateur non trouvé")
    })
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        String username = authentication.getName().toLowerCase(); // Convertir en minuscules si nécessaire
        Optional<User> userOpt = userService.findByEmail(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }
        User user = userOpt.get();
        UserDto userDto = modelMapper.map(user, UserDto.class); // Utiliser ModelMapper pour la cohérence
        return ResponseEntity.ok(userDto);
    }


}