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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RentalServiceImpl implements RentalService {
    @Value("http://localhost:4200")
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
            User owner = userRepository.findAll().stream()
                    .filter(user -> user.getEmail().equals(email))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("User not found with email : " + email));
            rentalRequestDto.setOwner_id(owner.getId());

            // Save the image file
            MultipartFile picture = rentalRequestDto.getPicture();
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(picture.getOriginalFilename()));
            Path path = Paths.get("src/main/resources/static/images/" + fileName);
            Files.copy(picture.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = serverUrl + "/" + fileName;

            // Create a RentalDTO from the RentalRequestDTO
            RentalDto rentalDto = new RentalDto();
            rentalDto.setId(rentalRequestDto.getId());
            rentalDto.setName(rentalRequestDto.getName());
            rentalDto.setSurface(rentalRequestDto.getSurface());
            rentalDto.setPrice(rentalRequestDto.getPrice());
            rentalDto.setPicture(imageUrl); // Set the image URL
            rentalDto.setDescription(rentalRequestDto.getDescription());
            rentalDto.setOwner_id(owner.getId());

            Rental rental = modelMapper.map(rentalDto, Rental.class);
            rental.setOwner(owner);
            rental.setPicture(rentalDto.getPicture());
            Rental savedRental = rentalRepository.save(rental);
            RentalDto savedRentalDto = modelMapper.map(savedRental, RentalDto.class);
            savedRentalDto.setOwner_id(savedRental.getOwner().getId());
            savedRentalDto.setId(savedRental.getId()); // Set the id in the RentalDTO
        } catch (Exception e) {
            throw new RuntimeException("Error while creating rental: " + e.getMessage());
        }
    }

    @Override
    public RentalDto getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found with id: " + id));

        return modelMapper.map(rental, RentalDto.class);
    }

    @Override
    public List<RentalDto> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        return rentals.stream()
                .map(rental -> modelMapper.map(rental, RentalDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RentalDto updateRental(Long id, RentalRequestDto rentalRequestDTO) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found with id: " + id));

        modelMapper.map(rentalRequestDTO, rental);
        rental.setUpdated_at(new Date());
        Rental updatedRental = rentalRepository.save(rental);

        return modelMapper.map(updatedRental, RentalDto.class);
    }

    @Override
    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }
}

