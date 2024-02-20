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
    @Operation(summary = "Create a new message", responses = {@ApiResponse(responseCode = "200", description = "Message created successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "SuccessResponse", value = "{\"message\": \"Message send with success\"}"))), @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "BadRequestError", value = "{\"error\": \"Error message for bad request\"}"))), @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "UnauthorizedError", value = "{\"error\": \"Unauthorized: Sorry You are not allow here \"}"))), @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "NotFoundError", value = "{\"error\": \"Not Found: Relevant error message\"}")))})
    public ResponseEntity<?> createMessage(@Valid @RequestBody MessageDto messageDto) {
        try {
            // Vérification d'authentification pour un 401
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();  // On récupère l'objet Authentication de sécurité de Spring Security. Il contient les détails de l'authentification de l'utilisateur actuel.
            if (authentication == null || !authentication.isAuthenticated()) {                       // Vérifie si l'objet Authentication est null ou si l'utilisateur n'est pas authentifié
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Sorry You are not allow here ");
            }

            // Logique pour un succès, renvoie un 200
            messageService.saveMessage(messageDto);
            Map<String, String> responseMessage = new HashMap<>();                  // Crée une nouvelle instance de HashMap pour stocker la réponse.
            responseMessage.put("message", "Message send with success");            //  Ajoute une paire clé-valeur à la map, avec "message" comme clé et "Message send with success" comme valeur.
            return ResponseEntity.ok(responseMessage);

            // Gestion d'une violation de contrainte, renvoie un 400
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());

            // Gestion d'une ressource non trouvée, renvoie un 404
        } catch (NoSuchElementException e) {
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