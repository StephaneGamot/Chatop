package com.chatop.backend.service.service;

import com.chatop.backend.dto.MessageDto;

public interface MessageService {
    void saveMessage(MessageDto messageDto);
}

