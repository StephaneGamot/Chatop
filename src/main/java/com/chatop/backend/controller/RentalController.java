package com.chatop.backend.controller;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDTO;
import com.chatop.backend.model.Rental;
import com.chatop.backend.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RentalController {

    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Récupération de toutes les locations", description = "Retourne une liste de toutes les locations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des locations retournée avec succès"),
            @ApiResponse(responseCode = "400", description = "Probleme dans la requete"),
            @ApiResponse(responseCode = "404", description = "Aucune 'location' retrouvée")
    })
    @GetMapping
    public ResponseEntity<List<RentalDto>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @Operation(summary = "Récupération d'une location par son ID", description = "Retourne les détails d'une location spécifique grâce à son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location trouvée et retournée avec succès"),
            @ApiResponse(responseCode = "400", description = "Problème avec l'Id fourni"),
            @ApiResponse(responseCode = "404", description = "Location non trouvée")
    })
    @GetMapping("/rentals/{id}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body(null);
        }
        Rental rental = rentalService.getRentalById(id);
        if (rental == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rental);
    }

    @Operation(summary = "Création d'une nouvelle location", description = "Création d'une nouvelle location et la retourne")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nouvelle location créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Probleme dans la requete lors de la création d'une nouvelle location"),
    })
    @PostMapping( "/rentals")
    public ResponseEntity<?> createRental(@ModelAttribute RentalRequestDTO rentalRequestDTO, Principal principal) {
        try {
            RentalDto createdRental = rentalService.createRental(rentalRequestDTO, principal);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRental);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while creating rental: " + e.getMessage());
        }
    }


    @Operation(summary = "Mettre à jour d'une location", description = "Mise à jour les détails d'une location existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Succès de la mise à jour de 'Location'"),
            @ApiResponse(responseCode = "404", description = "Mise à jour de 'Location' non trouvée")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RentalDto> updateRental(@PathVariable Long id, @RequestBody RentalRequestDTO rentalRequestDTO) {
        try {
            RentalDto updatedRental = rentalService.updateRental(id, rentalRequestDTO);
            return ResponseEntity.ok(updatedRental);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    }
