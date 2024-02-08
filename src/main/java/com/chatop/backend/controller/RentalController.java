package com.chatop.backend.controller;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDto;
import com.chatop.backend.service.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping(value = "")
    public ResponseEntity<Object> createRental(@ModelAttribute RentalRequestDto rentalRequestDto, Principal principal) {
        try {
            rentalService.createRental(rentalRequestDto, principal);
            return new ResponseEntity<>(Collections.singletonMap("message", "Rental created !"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Error while creating rental: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalDto> getRental(@PathVariable("id") final Long id) {
        try {
            RentalDto rentalDto = rentalService.getRentalById(id);
            return ResponseEntity.ok(rentalDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("")
    public ResponseEntity<List<RentalDto>> getRentals() {
        List<RentalDto> rentals = rentalService.getAllRentals();
        return ResponseEntity.ok(rentals);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRental(@PathVariable("id") Long id, @ModelAttribute RentalRequestDto rentalRequestDTO) {
        try {
            RentalDto updatedRental = rentalService.updateRental(id, rentalRequestDTO);
            return ResponseEntity.ok(updatedRental);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Error while updating rental: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Si vous avez une méthode pour supprimer des locations, elle devrait ressembler à cela :
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRental(@PathVariable Long id) {
        try {
            rentalService.deleteRental(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Rental deleted successfully!"));
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Error while deleting rental: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
