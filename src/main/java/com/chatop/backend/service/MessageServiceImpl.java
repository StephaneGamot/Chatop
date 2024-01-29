package com.chatop.backend.service;

import com.chatop.backend.dto.MessageDto;
import com.chatop.backend.model.Message;
import com.chatop.backend.repository.MessageRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public MessageDto createMessage(MessageDto messageDto) {
        Message message = new Message();
        BeanUtils.copyProperties(messageDto, message);
        Message savedMessage = messageRepository.save(message);
        BeanUtils.copyProperties(savedMessage, messageDto);
        return messageDto;
    }

    // Implémentez d'autres méthodes si nécessaire
}
