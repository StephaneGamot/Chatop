package com.chatop.backend.service.serviceImpl;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDto;
import com.chatop.backend.model.Rental;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.RentalRepository;
import com.chatop.backend.repository.UserRepository;
import com.chatop.backend.service.service.RentalService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RentalServiceImpl implements RentalService {
    @Value("${server.url}")
    private String serverUrl;

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public RentalServiceImpl(RentalRepository rentalRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public void createRental(RentalRequestDto rentalRequestDto, Principal principal) {
        try {
            String email = principal.getName();
            User owner = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
            rentalRequestDto.setOwner_id(owner.getId());

            // Traitement de l'image
            if (rentalRequestDto.getPicture() != null && !rentalRequestDto.getPicture().isEmpty()) {
                String imageUrl = saveImage(rentalRequestDto.getPicture());
                rentalRequestDto.setPictureUrl(imageUrl);
            }

            Rental rental = convertToEntity(rentalRequestDto);
            rental.setOwner(owner);
            rentalRepository.save(rental);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating rental: " + e.getMessage(), e);
        }
    }

    public String saveImage(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path uploadPath = Paths.get("src/main/resources/static/images/");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Utiliser serverUrl pour créer l'URL complète de l'image
        return serverUrl + "/images/" + fileName; // Retourne l'URL complète
    }


    private Rental convertToEntity(RentalRequestDto rentalRequestDto) {
        Rental rental = modelMapper.map(rentalRequestDto, Rental.class);
        if (rentalRequestDto.getPictureUrl() != null) {
            rental.setPicture(rentalRequestDto.getPictureUrl());
        }
        return rental;
    }

    @Override
    public RentalDto getRentalById(Long id) {
        Optional<Rental> rental = rentalRepository.findById(id);
        return rental.map(r -> {
            RentalDto rentalDto = modelMapper.map(r, RentalDto.class);
            rentalDto.setOwner_id(r.getOwner().getId());
            rentalDto.setPicture(r.getPicture());
            rentalDto.setCreated_at(r.getCreated_at());
            rentalDto.setUpdated_at(r.getUpdated_at());
            return rentalDto;
        }).orElse(null);
    }

    @Override
    public List<RentalDto> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        return rentals.stream()
                .map(rental -> modelMapper.map(rental, RentalDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RentalDto updateRental(Long id, RentalRequestDto rentalDto) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found with id: " + id));

        modelMapper.map(rentalDto, rental);
        rental.setUpdated_at(new Date());
        Rental updatedRental = rentalRepository.save(rental);

        return modelMapper.map(updatedRental, RentalDto.class);
    }

    @Override
    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }
}

