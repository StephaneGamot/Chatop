package com.chatop.backend.service;

import com.chatop.backend.dto.MessageDto;
import com.chatop.backend.model.Message;
import com.chatop.backend.repository.MessageRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        if (message.getCreatedAt() == null) {
            message.setCreatedAt(new Date());
        }
        Message savedMessage = messageRepository.save(message);
        BeanUtils.copyProperties(savedMessage, messageDto);
        return messageDto;
    }
}
