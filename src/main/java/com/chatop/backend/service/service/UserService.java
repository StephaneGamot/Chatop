package com.chatop.backend.service.service;

import com.chatop.backend.dto.AuthResponseDto;
import com.chatop.backend.dto.AuthLoginDto;
import com.chatop.backend.dto.AuthRegisterDto;
import com.chatop.backend.dto.UserDto;
import com.chatop.backend.model.User;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto registerUser(AuthRegisterDto authRegisterDto) throws IOException;
    UserDto findUserById(Long id) throws IOException;
    List<UserDto> findAllUsers() throws IOException;
    Optional<User> findUserByEmail(String email) throws IOException;
    // Mettez à jour cette méthode pour accepter Authentication
    UserDto getCurrentUser(Authentication authentication) throws IOException;
    AuthResponseDto loginUser(AuthLoginDto authLoginDto) throws IOException;
}

