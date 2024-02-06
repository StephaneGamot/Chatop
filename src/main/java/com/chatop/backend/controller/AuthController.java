package com.chatop.backend.controller;


import com.chatop.backend.dto.AuthLoginDto;
import com.chatop.backend.dto.AuthRegisterDto;
import com.chatop.backend.dto.AuthResponseDto;
import com.chatop.backend.dto.UserDto;
import com.chatop.backend.model.User;
import com.chatop.backend.security.JwtService;
import com.chatop.backend.security.*;
import com.chatop.backend.service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

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
    public ResponseEntity<?> registerUser(@RequestBody AuthRegisterDto authRegisterDto) throws IOException {
        userService.registerUser(authRegisterDto);
        AuthLoginDto authLoginDto = new AuthLoginDto(authRegisterDto.getEmail(), authRegisterDto.getPassword());
        AuthResponseDto authenticationResponse = userService.loginUser(authLoginDto);
        return ResponseEntity.ok(Collections.singletonMap("token", authenticationResponse.getToken()));
    }



    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthLoginDto authLoginDto) {
        try {
            AuthResponseDto authenticationResponse = userService.loginUser(authLoginDto);
            return ResponseEntity.ok(Collections.singletonMap("token", authenticationResponse.getToken()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            UserDto userDto = userService.getCurrentUser(authentication);
            return ResponseEntity.ok(userDto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching the current user.");
        }
    }



}
