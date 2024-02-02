package com.chatop.backend.controller;

import com.chatop.backend.dto.UserDto;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.UserRepository;
import com.chatop.backend.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.chatop.backend.service.JWTService;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;


    @Operation(summary = "Inscription d'un nouvel utilisateur", description = "Crée un nouvel utilisateur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Problème avec la requète 'création d'un utilisateur'"),
            @ApiResponse(responseCode = "409", description = "Nom d'utilisateur déjà utilisé"),
            @ApiResponse(responseCode = "500", description = "Erreur provenant du serveur")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid User user) {
        UserDto savedUserDto = userService.registerUser(user);
        if (savedUserDto == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email déjà utilisé");
        }
        String token = jwtService.generateTokenForUser(savedUserDto);
        savedUserDto.setToken(token);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).body(savedUserDto);
    }

    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Non autorisé - Échec de l'authentification"),
            @ApiResponse(responseCode = "404", description = "L'utilisateur n'a pas été retrouvé")
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(Authentication authentication) {
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = jwtService.generateToken(authentication);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Obtention des informations de l'utilisateur", description = "Retourne les informations de l'utilisateur actuellement authentifié")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations de l'utilisateur récupérées avec succès"),
            @ApiResponse(responseCode = "401", description = "Non autorisé - Utilisateur non authentifié"),
            @ApiResponse(responseCode = "404", description = "Non trouvé - Utilisateur non trouvé")
    })
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        // Supposons que l'identifiant unique est l'email de l'utilisateur
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        try {
            Optional<User> userOpt = userService.findByEmail(email);

            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
            }

            // Convertissez l'entité User en UserDto (ou tout autre objet DTO que vous souhaitez renvoyer)
            UserDto userDto = convertToDTO(userOpt.get());

            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            // Loggez l'erreur si nécessaire
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de la récupération des informations de l'utilisateur");
        }
    }

    private UserDto convertToDTO(User user) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(user, UserDto.class);
    }


}