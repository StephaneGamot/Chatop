package com.chatop.backend.controller;

import com.chatop.backend.dto.UserDto;
import com.chatop.backend.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", responses = {@ApiResponse(responseCode = "200", description = "User found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class), examples = {@ExampleObject(name = "SuccessResponse", value = "{\"id\": 1, \"email\": \"example@example.com\", \"name\": \"John Doe\"}")})), @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {@ExampleObject(name = "UnauthorizedResponse", value = "{\"error\": \"Unauthorized: Access is denied due to invalid credentials.\"}")})), @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class), examples = {@ExampleObject(name = "NotFoundResponse", value = "{\"error\": \"User not found.\"}")}))})

    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
        UserDto userDto = userService.findUserById(id);
        return ResponseEntity.ok(userDto);
    }
}

/*
 * L'erreur 401 est géré par Spring security ( en cas de soucis avec le token )
 * L'erreur 404 est géré par  Tomcat, si il ne trouve pas la route alors il renvoie vers 404
 * */