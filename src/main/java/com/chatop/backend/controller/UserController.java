package com.chatop.backend.controller;


import com.chatop.backend.dto.UserDto;
import com.chatop.backend.service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
        UserDto userDto = userService.findUserById(id);
        return ResponseEntity.ok(userDto);
    }
}
