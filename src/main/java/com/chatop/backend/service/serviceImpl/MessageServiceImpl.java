package com.chatop.backend.service.serviceImpl;

import com.chatop.backend.dto.MessageDto;
import com.chatop.backend.model.Message;
import com.chatop.backend.model.Rental;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.MessageRepository;
import com.chatop.backend.repository.RentalRepository;
import com.chatop.backend.repository.UserRepository;
import com.chatop.backend.service.service.MessageService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final ModelMapper modelMapper;   // C'est un outil pour transformer les objets DTO en entités

    // Injection de toutes les dépendances via le constructeur
    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, RentalRepository rentalRepository, ModelMapper modelMapper) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void saveMessage(MessageDto messageDto) {
        // Recherche de l'utilisateur et de la location pour s'assurer qu'ils existent
        User user = userRepository.findById(messageDto.getUser_id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id : " + messageDto.getUser_id()));
        Rental rental = rentalRepository.findById(messageDto.getRental_id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rental not found with id : " + messageDto.getRental_id()));

        // Utilisation de ModelMapper pour convertir MessageDto en Message
        Message message = modelMapper.map(messageDto, Message.class);
        message.setUser(user);
        message.setRental(rental);

        // Définir les dates de création et de mise à jour
        message.setCreated_at(new Date());
        message.setUpdated_at(new Date());

        // Sauvegarde du message
        messageRepository.save(message);
    }
}
