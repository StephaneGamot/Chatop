package com.chatop.backend.controller;

import com.chatop.backend.dto.AuthLoginDto;
import com.chatop.backend.dto.AuthRegisterDto;
import com.chatop.backend.dto.AuthResponseDto;
import com.chatop.backend.dto.UserDto;
import com.chatop.backend.security.JwtService;
import com.chatop.backend.service.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;

import jakarta.validation.Valid;

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
    public ResponseEntity<?> registerUser(@Valid @RequestBody AuthRegisterDto authRegisterDto) {
        try {
            UserDto registeredUser = userService.registerUser(authRegisterDto);
            // Création d'un token pour l'utilisateur nouvellement enregistré directement
            String token = jwtService.generateToken(new UsernamePasswordAuthenticationToken(
                    registeredUser.getEmail(), null, Collections.emptyList())); // Ajustez selon votre implémentation de JwtService

            // On retourne la réponse avec le token
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error during registration: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An error occurred during registration.");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthLoginDto authLoginDto) {
        try {
            AuthResponseDto authenticationResponse = userService.loginUser(authLoginDto);
            return ResponseEntity.ok(Collections.singletonMap("token", authenticationResponse.getToken()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error during login: " + e.getMessage());
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        // Si l'authentification n'est pas présente, renvoie un 401
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Authentication is required.");
        }
        try {
            UserDto userDto = userService.getCurrentUser(authentication);
            // Si l'utilisateur est trouvé, il renvoie un 200 avec les données de l'utilisateur
            return ResponseEntity.ok(userDto);
        } catch (UsernameNotFoundException e) {
            // Si l'utilisateur n'est pas trouvé, renvoie un 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found: User not found.");
        } catch (Exception e) {
            // Pour tout autre type d'erreur, on renvoie un 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}
