package com.chatop.backend.service;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDTO;
import com.chatop.backend.model.Rental;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.RentalRepository;
import com.chatop.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;


@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(RentalServiceImpl.class);


    public RentalServiceImpl(RentalRepository rentalRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<RentalDto> getAllRentals() {
        return rentalRepository.findAll().stream().map(rental -> {
            RentalDto dto = new RentalDto();
            BeanUtils.copyProperties(rental, dto);
            dto.setOwnerId(rental.getOwner().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    @Override
    public RentalDto createRental(RentalRequestDTO rentalRequestDTO, Principal principal) throws IOException {
        // Logger pour la journalisation
        Logger logger = LoggerFactory.getLogger(this.getClass());

        // Obtenez l'email à partir de principal
        String username = principal.getName();
        logger.info("Tentative de création d'une location pour le nom d'utilisateur: {}", username);

        User owner;
        try {
            owner = userRepository.findByName(username)
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec le nom d'utilisateur: " + username));
        } catch (EntityNotFoundException ex) {
            logger.error("Erreur lors de la récupération de l'utilisateur avec le nom d'utilisateur {}", username, ex);
            throw ex;
        }

        // Créez et configurez l'objet Rental
        Rental rental = new Rental();
        rental.setName(rentalRequestDTO.getName());
        rental.setSurface(rentalRequestDTO.getSurface());
        rental.setPrice(rentalRequestDTO.getPrice());
        rental.setDescription(rentalRequestDTO.getDescription());
        rental.setOwner(owner);

        // Gestion de l'upload de l'image
        if (rentalRequestDTO.getPicture() != null && !rentalRequestDTO.getPicture().isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(rentalRequestDTO.getPicture().getOriginalFilename()));
            Path path = Paths.get("/img" + fileName);
            Files.copy(rentalRequestDTO.getPicture().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            rental.setPicture(path.toString());
        }

        Rental savedRental;
        try {
            savedRental = rentalRepository.save(rental);
        } catch (Exception ex) {
            logger.error("Erreur lors de l'enregistrement de la location", ex);
            throw ex; // ou gérer autrement si nécessaire
        }

        // Création d'un objet RentalDto pour le retour
        RentalDto rentalDto = new RentalDto();
        BeanUtils.copyProperties(savedRental, rentalDto);
        rentalDto.setOwnerId(savedRental.getOwner().getId());

        return rentalDto;
    }

    @Override
    public Rental updateRental(Long id, Rental rentalDetails) {
        return rentalRepository.findById(id).map(existingRental -> {
            existingRental.setName(rentalDetails.getName());
            existingRental.setSurface(rentalDetails.getSurface());   // Répétez pour les autres champs
            existingRental.setPrice(rentalDetails.getPrice());
            existingRental.setPicture(rentalDetails.getPicture());
            existingRental.setDescription(rentalDetails.getDescription());
            return rentalRepository.save(existingRental);
        }).orElseThrow(() -> new EntityNotFoundException("Rental not found with id: " + id));
    }
}
