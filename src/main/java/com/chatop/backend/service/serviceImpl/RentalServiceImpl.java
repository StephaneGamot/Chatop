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
        return rentals.stream().map(rental -> {
            RentalDto rentalDto = modelMapper.map(rental, RentalDto.class);
            if (rental.getOwner() != null) {
                rentalDto.setOwner_id(rental.getOwner().getId());
            }
            return rentalDto;
        }).collect(Collectors.toList());
    }


    @Override
    public RentalDto updateRental(Long id, RentalRequestDto rentalRequestDto) {
        Rental existingRental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found with id: " + id));

        // Conserve l'URL de l'image existante avant le mappage
        String existingImageUrl = existingRental.getPicture();

        // Mappe les propriétés à partir de rentalRequestDto vers existingRental,
        // sans toucher au champ de l'image.
        modelMapper.map(rentalRequestDto, existingRental);

        // Réaffecte l'URL de l'image existante après le mappage
        // pour s'assurer qu'elle n'est pas modifiée ou perdue.
        existingRental.setPicture(existingImageUrl);

        existingRental.setUpdated_at(new Date());
        Rental updatedRental = rentalRepository.save(existingRental);

        // Assurez-vous que le RentalDto retourné contient également l'URL de l'image.
        RentalDto rentalDto = modelMapper.map(updatedRental, RentalDto.class);
        rentalDto.setPicture(existingImageUrl); // Assurez-vous que l'URL de l'image est correctement définie dans le DTO

        return rentalDto;
    }

    @Override
    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }
}

