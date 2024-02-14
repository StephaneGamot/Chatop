package com.chatop.backend.controller;

import com.chatop.backend.dto.*;
import com.chatop.backend.security.JwtService;
import com.chatop.backend.service.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ExampleObject;

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
    @Operation(summary = "Register a new user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name="SuccessResponse", value="{\"token\": \"your_generated_token_here\"}")
                                    })),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name="ErrorResponse", value="{\"error\": \"Error message\"}")
                                    }))
            })
    public ResponseEntity<?> registerUser(@Valid @RequestBody AuthRegisterDto authRegisterDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto("error"));
        }
        try {
            UserDto registeredUser = userService.registerUser(authRegisterDto);
            // Création d'un token pour le nouvel l'utilisateur via JwtService
            String token = jwtService.generateToken(new UsernamePasswordAuthenticationToken(
                    registeredUser.getEmail(), null, Collections.emptyList()));

            // On retourne la réponse avec le token
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error during registration: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An error occurred during registration.");
        }
    }


    @PostMapping("/login")
    @Operation(summary = "Log in a user and return a JWT token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged in successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name="SuccessResponse", value="{\"token\": \"your_generated_token_here\"}")
                                    })),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials or login error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name="ErrorResponse", value="{\"error\": \"Invalid credentials.\"}")
                                    }))
            })
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthLoginDto authLoginDto, Errors errors) {
       if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto("error"));
        }
        try {
            AuthResponseDto authenticationResponse = userService.loginUser(authLoginDto);
            return ResponseEntity.ok(authenticationResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }
    }


    @GetMapping("/me")
    @Operation(summary = "Get the current authenticated user's information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authenticated user's information returned successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized: Authentication is required",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name="UnauthorizedResponse", value="{\"error\": \"Unauthorized: Authentication is required.\"}")
                                    })),
                    @ApiResponse(responseCode = "404", description = "Not Found: User not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class),
                                    examples = {
                                            @ExampleObject(name="NotFoundResponse", value="{\"error\": \"User not found.\"}")
                                    }))
            })
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        // Si l'authentification n'est pas présente, renvoie un 401
        if (!authentication.isAuthenticated()) {
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
