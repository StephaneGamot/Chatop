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
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Value("${server.url}")
    private String serverUrl;

    public RentalServiceImpl(RentalRepository rentalRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    // Méthode pour créer une nouvelle location
    public void createRental(RentalRequestDto rentalRequestDto, Principal principal) {

        // Je trouve l'utilisateur par son email et l'associe à la location
        try {
            String email = principal.getName();
            User owner = userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("User not found with email: " + email));
            rentalRequestDto.setOwner_id(owner.getId());   // spécifie qui est le propriétaire de la location

            // Si une image est fournie, je la sauvegarde et défini son l'URL
            if (rentalRequestDto.getPicture() != null && !rentalRequestDto.getPicture().isEmpty()) {
                String imageUrl = saveImage(rentalRequestDto.getPicture());
                rentalRequestDto.setPictureUrl(imageUrl);
            }

            // Je convertie le DTO en entité et le sauvegarde
            Rental rental = convertToEntity(rentalRequestDto);
            rental.setOwner(owner);
            rentalRepository.save(rental);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating rental: " + e.getMessage(), e);
        }
    }

    // Méthode pour sauvegarder une image reçue et retourner son URL
    public String saveImage(MultipartFile file) throws IOException {

        // Nettoie le nom du fichier pour éviter les chemins relatifs qui pourraient mener à des vulnérabilités.
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Définit le chemin où les images seront sauvegardées dans le projet.
        Path uploadPath = Paths.get("src/main/resources/static/images/");

        // Si le chemin d'upload n'existe pas, crée les dossiers nécessaires.
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Copie le fichier uploadé dans le chemin spécifié, remplaçant un fichier existant avec le même nom.
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Construit l'URL pour accéder à l'image via le serveur et la retourne.
        return serverUrl + "/images/" + fileName;
    }

    // Méthode pour convertir un DTO de location en entité
    private Rental convertToEntity(RentalRequestDto rentalRequestDto) {
        Rental rental = modelMapper.map(rentalRequestDto, Rental.class);  // Utilise ModelMapper pour transformer les données reçues (DTO) en une nouvelle entité Rental.
        if (rentalRequestDto.getPictureUrl() != null) {                   //  si une URL d'image est fournie dans le DTO et, si oui, la définit pour l'entité Rental.
            rental.setPicture(rentalRequestDto.getPictureUrl());
        }
        return rental;
    }

    // Méthode pour récupérer une location par son ID
    @Override
    public RentalDto getRentalById(Long id) {
        Optional<Rental> rental = rentalRepository.findById(id);      // Essaie de trouver une location par son ID dans la base de données.
        return rental.map(r -> {                                      // Si une location est trouvée, transforme cette location en un DTO.
            RentalDto rentalDto = modelMapper.map(r, RentalDto.class);// Utilise ModelMapper pour convertir l'entité Rental trouvée en un objet RentalDto.
            rentalDto.setOwner_id(r.getOwner().getId());              // Définit manuellement l'ID du propriétaire,
            rentalDto.setPicture(r.getPicture());                     // Définit manuellement l'image,
            rentalDto.setCreated_at(r.getCreated_at());               // Définit manuellement dates de création,
            rentalDto.setUpdated_at(r.getUpdated_at());               // Définit manuellement sa date de mise à jour ,
            return rentalDto;                                         // Retourne l'objet DTO complété.
        }).orElse(null);
    }

    // Méthode pour récupérer toutes les locations
    @Override
    public List<RentalDto> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();                   // Récupère toutes les locations de la base de données.
        return rentals.stream().map(rental -> {                              // Transforme chaque entité Rental en un RentalDto.
            RentalDto rentalDto = modelMapper.map(rental, RentalDto.class);
            if (rental.getOwner() != null) {                                 // Si l'entité Rental a un propriétaire, définit l'ID du propriétaire dans le DTO.
                rentalDto.setOwner_id(rental.getOwner().getId());
            }
            return rentalDto;
        }).collect(Collectors.toList());                                     // Collecte tous les DTO dans une liste et la retourne.
    }

    // Méthode pour mettre à jour une location existante
    @Override
    public RentalDto updateRental(Long id, RentalRequestDto rentalRequestDto) {
        Rental existingRental = rentalRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Rental not found with id: " + id));

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

        RentalDto rentalDto = modelMapper.map(updatedRental, RentalDto.class);
        rentalDto.setPicture(existingImageUrl);

        return rentalDto;
    }

    @Override
    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }
}

