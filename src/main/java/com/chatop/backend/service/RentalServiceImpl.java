package com.chatop.backend.service;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDTO;
import com.chatop.backend.model.Rental;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.RentalRepository;
import com.chatop.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UploadFileService uploadFileService;

    @Autowired
    public RentalServiceImpl(RentalRepository rentalRepository, UserRepository userRepository, UploadFileService uploadFileService) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.uploadFileService = uploadFileService;
    }
    private static final Logger logger = LoggerFactory.getLogger(RentalServiceImpl.class);

    private String saveImage(MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
            Path uploadPath = Paths.get("uploads"); // Assurez-vous que ce chemin existe sur votre serveur
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toUri().toString(); // Construit l'URL de l'image
        }
        return null;
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
    public RentalDto createRental(String name, int surface, int price, MultipartFile picture, String description, Long owner_id) throws IOException {
        Logger logger = LoggerFactory.getLogger(this.getClass());
        String pictureUrl = uploadFileService.uploadFile(picture);

        User owner = userRepository.findById(owner_id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + owner_id));

        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        rental.setOwner(owner);
        rental.setPicture(pictureUrl);

        Rental savedRental;
        try {
            savedRental = rentalRepository.save(rental);
        } catch (Exception ex) {
            logger.error("Erreur lors de l'enregistrement de la location", ex);
            throw ex;
        }

        RentalDto rentalDto = new RentalDto();
        BeanUtils.copyProperties(savedRental, rentalDto);
        rentalDto.setOwnerId(owner.getId()); // Utilisez l'ID de l'objet owner

        return rentalDto;
    }


    @Override
    public RentalDto updateRental(Long id, RentalRequestDTO rentalRequestDTO) throws IOException {
        Logger logger = LoggerFactory.getLogger(this.getClass());

        // Rechercher le Rental existant par son ID
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location non trouvée avec l'id: " + id));

        // Mise à jour des propriétés de l'objet Rental
        rental.setName(rentalRequestDTO.getName());
        rental.setSurface(rentalRequestDTO.getSurface());
        rental.setPrice(rentalRequestDTO.getPrice());
        rental.setDescription(rentalRequestDTO.getDescription());

        String imageUrl = saveImage(rentalRequestDTO.getPicture());
        if (imageUrl != null) {
            rental.setPicture(imageUrl);
        }

        // Enregistrement des modifications
        try {
            rental = rentalRepository.save(rental);
        } catch (Exception ex) {
            logger.error("Erreur lors de la mise à jour de la location", ex);
            throw ex;
        }

        // Conversion en DTO pour le retour
        RentalDto rentalDto = new RentalDto();
        BeanUtils.copyProperties(rental, rentalDto);
        rentalDto.setOwnerId(rental.getOwner().getId());

        return rentalDto;
    }
}
