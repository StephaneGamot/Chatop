package com.chatop.backend.service.service;

import com.chatop.backend.dto.MessageDto;
public interface MessageService {
    MessageDto createMessage(Long rentalId, Long userId, String message);

}
