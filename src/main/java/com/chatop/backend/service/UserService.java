package com.chatop.backend.service;

import com.chatop.backend.model.User;

import java.util.List;

public interface UserService {
        User registerUser(User user);
        User findUserByName(String name);
        User findUserById(Long id);
        List<User> findAllUsers();
    }

