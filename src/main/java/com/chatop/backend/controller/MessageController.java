package com.chatop.backend.controller;

import com.chatop.backend.model.Message;
import com.chatop.backend.dto.MessageDto;
import com.chatop.backend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Création d'un message", description = "Création d'un nouveau message et le retourne")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message créé avec succès")

    })
    @PostMapping
    public ResponseEntity<MessageDto> createMessage(@RequestBody MessageDto messageDto) {
        return ResponseEntity.ok(messageService.createMessage(messageDto));
    }
}

