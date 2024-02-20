package com.chatop.backend.controller;

import com.chatop.backend.dto.*;
import com.chatop.backend.security.JwtService;
import com.chatop.backend.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", responses = {@ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {@ExampleObject(name = "SuccessResponse", value = "{\"token\": \"your_generated_token_here\"}")})), @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {@ExampleObject(name = "ErrorResponse", value = "{\"error\": \"Error message\"}")}))})
    public ResponseEntity<?> registerUser(@Valid @RequestBody AuthRegisterDto authRegisterDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto("error"));
        }
        // Tente d'inscrire l'utilisateur en utilisant userService et stocke le résultat dans registeredUser.
        try {
            UserDto registeredUser = userService.registerUser(authRegisterDto);
            String token = jwtService.generateToken(new UsernamePasswordAuthenticationToken(                    // Création d'un token pour le nouvel l'utilisateur via JwtService
                    registeredUser.getEmail(), null, Collections.emptyList()));                       //  Le token est basé sur l'email de l'utilisateur. Ce token est nécessaire pour que l'utilisateur puisse s'authentifier.

            return ResponseEntity.ok(Collections.singletonMap("token", token));                                 // On retourne la réponse avec le token
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error during registration: " + e.getMessage());
        } catch (
                Exception e) {                                                                                 //Gestion de toute autre exception non spécifiée.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An error occurred during registration.");
        }
    }


    @PostMapping("/login")
    @Operation(summary = "Log in a user and return a JWT token", responses = {@ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {@ExampleObject(name = "SuccessResponse", value = "{\"token\": \"your_generated_token_here\"}")})), @ApiResponse(responseCode = "401", description = "Invalid credentials or login error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {@ExampleObject(name = "ErrorResponse", value = "{\"error\": \"Invalid credentials.\"}")}))})
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthLoginDto authLoginDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto("error"));
        }
        try {
            AuthResponseDto authenticationResponse = userService.loginUser(authLoginDto);  // Tente de connecter l'utilisateur et de récupérer la réponse d'authentification.
            return ResponseEntity.ok(authenticationResponse);                              // Retourne la réponse d'authentification si la connexion est réussie.
        } catch (
                AuthenticationException e) {                                              // Gère les exceptions d'authentification.

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }
    }


    @GetMapping("/me")
    @Operation(summary = "Get the current authenticated user's information", responses = {@ApiResponse(responseCode = "200", description = "Authenticated user's information returned successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))), @ApiResponse(responseCode = "401", description = "Unauthorized: Authentication is required", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {@ExampleObject(name = "UnauthorizedResponse", value = "{\"error\": \"Unauthorized: Authentication is required.\"}")})), @ApiResponse(responseCode = "404", description = "Not Found: User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {@ExampleObject(name = "NotFoundResponse", value = "{\"error\": \"User not found.\"}")}))})
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        if (!authentication.isAuthenticated()) {                // Vérifie si l'utilisateur est authentifié.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Authentication is required.");
        }
        try {
            UserDto userDto = userService.getCurrentUser(authentication);   // Tente d'obtenir les informations de l'utilisateur courant.
            // Si l'utilisateur est trouvé, il renvoie un 200 avec les données de l'utilisateur
            return ResponseEntity.ok(userDto);
        } catch (
                UsernameNotFoundException e) {                             // Gère l'exception si l'utilisateur n'est pas trouvé.
            // Si l'utilisateur n'est pas trouvé, renvoie un 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found: User not found.");
        } catch (Exception e) {
            // Pour tout autre type d'erreur, on renvoie un 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}

/*
 *  @Valid active la validation de l'objet AuthRegisterDto passé dans le corps de la requête.
 *
 * ResponseEntity : C'est une classe générique utilisée dans Spring Framework pour représenter une réponse HTTP complète, incluant le statut, les en-têtes, et le corps.
 * */