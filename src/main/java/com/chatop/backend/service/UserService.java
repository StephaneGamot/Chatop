package com.chatop.backend.service;

import com.chatop.backend.dto.UserDto;
import com.chatop.backend.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto registerUser(User user);
   // UserDto findUserByName(String name);
    UserDto findUserById(Long id);
        List< UserDto> findAllUsers();
    Optional<User> findByEmail(String email);
    }

