package com.chatop.backend.controller;

import com.chatop.backend.dto.UserDto;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.UserRepository;
import com.chatop.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.chatop.backend.service.JWTService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JWTService jwtService;
    private final UserService userService;

    public AuthController(JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.findUserByName(user.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nom d'utilisateur déjà utilisé");
        }
        User savedUser = userService.registerUser(user);
        String token = jwtService.generateTokenForUser(savedUser);

        return ResponseEntity.ok().body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return ResponseEntity.ok(jwtService.generateToken(authentication));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        String username = authentication.getName();
        User user = userService.findUserByName(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getName(), user.getCreatedAt(), user.getUpdatedAt());
        return ResponseEntity.ok(userDto);
    }

}