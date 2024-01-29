package com.chatop.backend.service;

import com.chatop.backend.dto.MessageDto;

public interface MessageService {
    MessageDto createMessage(MessageDto messageDto);
}
