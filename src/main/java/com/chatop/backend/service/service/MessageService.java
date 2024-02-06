package com.chatop.backend.service.service;

import com.chatop.backend.dto.MessageDto;

import java.io.IOException;

public interface MessageService {
    void saveMessage(MessageDto messageDto);
}

