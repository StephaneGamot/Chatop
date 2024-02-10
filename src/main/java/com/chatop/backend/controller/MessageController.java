package com.chatop.backend.controller;

import com.chatop.backend.dto.MessageDto;
import com.chatop.backend.service.service.MessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @Operation(summary = "Create a new message",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Message created successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "SuccessResponse", value = "{\"message\": \"Message send with success\"}"))),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "BadRequestError", value = "{\"error\": \"Error message for bad request\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UnauthorizedError", value = "{\"error\": \"Unauthorized: Sorry You are not allow here \"}"))),
                    @ApiResponse(responseCode = "404", description = "Not Found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "NotFoundError", value = "{\"error\": \"Not Found: Relevant error message\"}")))
            })
    public ResponseEntity<?> createMessage(@Valid @RequestBody MessageDto messageDto) {
        try {
            // Vérification d'authentification pour un 401
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Sorry You are not allow here ");
            }

            // Logique pour un succès, renvoie un 200
            messageService.saveMessage(messageDto);
            Map<String, String> responseMessage = new HashMap<>();
            responseMessage.put("message", "Message send with success");
            return ResponseEntity.ok(responseMessage);
        } catch (DataIntegrityViolationException e) {
            // Gestion d'une violation de contrainte, renvoie un 400
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (NoSuchElementException e) {
            // Gestion d'une ressource non trouvée, renvoie un 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found: " + e.getMessage());
        }
    }
}



/*
 * Je n 'utilise pas le modelMapper ici car je ne fais qu'envoyer un simple message
 *
 * @Valid sur @RequestBody MessageDto gere et renvoie un 400 Bad Request
 *
 * */