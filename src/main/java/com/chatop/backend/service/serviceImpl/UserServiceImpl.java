package com.chatop.backend.service.serviceImpl;

import com.chatop.backend.dto.AuthResponseDto;
import com.chatop.backend.dto.AuthLoginDto;
import com.chatop.backend.dto.AuthRegisterDto;
import com.chatop.backend.dto.UserDto;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.UserRepository;
import com.chatop.backend.security.JwtService;
import com.chatop.backend.service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;


import java.util.Date;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private UserDto convertToUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName(), user.getCreated_at(), user.getUpdated_at());
    }

    @Override
    public UserDto registerUser(AuthRegisterDto registerDto) throws IOException {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new IOException("Email already exists.");
        }

        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setName(registerDto.getName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setCreated_at(new Date());
        user.setUpdated_at(new Date());
        User savedUser = userRepository.save(user);

        return convertToUserDto(savedUser);
    }

    @Override
    public AuthResponseDto loginUser(AuthLoginDto loginDto) throws IOException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken(authentication);

        return new AuthResponseDto(token);
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return convertToUserDto(user);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDto getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return convertToUserDto(user);
    }
}

