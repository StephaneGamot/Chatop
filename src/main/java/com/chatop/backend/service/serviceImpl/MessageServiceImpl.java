package com.chatop.backend.service.serviceImpl;

import com.chatop.backend.dto.MessageDto;
import com.chatop.backend.model.Message;
import com.chatop.backend.model.Rental;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.MessageRepository;
import com.chatop.backend.repository.RentalRepository;
import com.chatop.backend.repository.UserRepository;
import com.chatop.backend.service.service.MessageService;

import java.util.Date;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, RentalRepository rentalRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public void saveMessage(MessageDto messageDto) {
        User user = userRepository.findById(messageDto.getUser_id())
                .orElseThrow(() -> new NoSuchElementException("User not found with id : " + messageDto.getUser_id()));
        Rental rental = rentalRepository.findById(messageDto.getRental_id())
                .orElseThrow(() -> new NoSuchElementException("Rental not found with id : " + messageDto.getRental_id()));

        Message message = new Message();
        message.setMessage(messageDto.getMessage());
        message.setUser(user);
        message.setRental(rental);
        message.setCreatedAt(new Date());
        message.setUpdatedAt(new Date());

        messageRepository.save(message);
    }
}
