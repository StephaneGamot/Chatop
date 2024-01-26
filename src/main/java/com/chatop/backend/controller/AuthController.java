package com.chatop.backend.controller;

import com.chatop.backend.model.User;
import com.chatop.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chatop.backend.service.JWTService;


@RestController
// @RequestMapping("/api/auth")
public class AuthController {

    private final JWTService jwtService;

    public AuthController(JWTService jwtService) {
        this.jwtService = jwtService;
    }



    @PostMapping("/login")
    public String getToken(Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authentication);
        } else {
            return "Utilisateur non authentifié";
        }
    }
}
