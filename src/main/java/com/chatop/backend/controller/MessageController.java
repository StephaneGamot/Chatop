package com.chatop.backend.controller;

import com.chatop.backend.dto.MessageResponse;
import com.chatop.backend.model.Message;
import com.chatop.backend.dto.MessageDto;
import com.chatop.backend.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public ResponseEntity<MessageResponse> createMessage(@RequestBody MessageDto messageDto) {
        messageService.createMessage(messageDto);
        return new ResponseEntity<>(new MessageResponse("Message send with success"), HttpStatus.CREATED);
    }
}

