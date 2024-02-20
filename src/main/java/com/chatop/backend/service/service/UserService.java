package com.chatop.backend.service.service;

import com.chatop.backend.dto.AuthLoginDto;
import com.chatop.backend.dto.AuthRegisterDto;
import com.chatop.backend.dto.AuthResponseDto;
import com.chatop.backend.dto.UserDto;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface UserService {
    UserDto registerUser(AuthRegisterDto authRegisterDto) throws IOException;

    UserDto findUserById(Long id);

    UserDto getCurrentUser(Authentication authentication) throws IOException;

    AuthResponseDto loginUser(AuthLoginDto authLoginDto);
}

