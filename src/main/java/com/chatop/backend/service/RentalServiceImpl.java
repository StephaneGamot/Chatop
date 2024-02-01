package com.chatop.backend.service;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDTO;
import com.chatop.backend.dto.UserDto;
import com.chatop.backend.model.Rental;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.RentalRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final UploadFileService uploadFileService; // Ajout de cette dépendance
    private final UserService userService;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository, UploadFileService uploadFileService, UserService userService) {
        this.rentalRepository = rentalRepository;
        this.uploadFileService = uploadFileService; // Initialisation de la dépendance
        this.userService = userService;
    }
    private static final Logger logger = LoggerFactory.getLogger(RentalServiceImpl.class);



    @Override
    public List<RentalDto> getAllRentals() {
        return rentalRepository.findAll().stream().map(rental -> {
            RentalDto dto = new RentalDto();
            BeanUtils.copyProperties(rental, dto);
            dto.setOwnerId(rental.getOwnerId().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    @Override
    public RentalDto createRental(String name, int surface, int price, String pictureUrl, String description, Long owner_id) throws IOException {
        UserDto ownerDto = userService.findUserById(owner_id);
        if (ownerDto == null) {
            throw new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + owner_id);
        }
        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        rental.setOwnerId(convertToEntity(ownerDto));
        rental.setPicture(pictureUrl); // Utilisation de pictureUrl directement
        rental.setCreatedAt(new Date());
        rental.setUpdatedAt(new Date());

        Rental savedRental = rentalRepository.save(rental);

        RentalDto rentalDto = new RentalDto();
        BeanUtils.copyProperties(savedRental, rentalDto);
        rentalDto.setOwnerId(owner_id);

        return rentalDto;
    }

    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        // user.setPassword(userDto.getPassword());
        user.setCreatedAt(userDto.getCreatedAt());
        user.setUpdatedAt(userDto.getUpdatedAt());
        return user;
    }

    @Override
    public RentalDto updateRental(Long id, RentalRequestDTO rentalRequestDTO) throws IOException {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location non trouvée avec l'id: " + id));

        rental.setName(rentalRequestDTO.getName());
        rental.setSurface(rentalRequestDTO.getSurface());
        rental.setPrice(rentalRequestDTO.getPrice());
        rental.setDescription(rentalRequestDTO.getDescription());

        // Gestion de l'image si nécessaire
        if (rentalRequestDTO.getPicture() != null && !rentalRequestDTO.getPicture().isEmpty()) {
            String imageUrl = uploadFileService.uploadFile(rentalRequestDTO.getPicture());
            rental.setPicture(imageUrl);
        }

        rental = rentalRepository.save(rental);

        RentalDto rentalDto = new RentalDto();
        BeanUtils.copyProperties(rental, rentalDto);
        rentalDto.setOwnerId(rental.getOwnerId().getId());

        return rentalDto;
    }

}
