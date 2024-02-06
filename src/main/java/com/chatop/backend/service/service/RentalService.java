package com.chatop.backend.service.service;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDto;
import com.chatop.backend.model.Rental;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface RentalService {
    RentalDto createRental(RentalRequestDto rentalRequestDTO, Principal principal);
    RentalDto getRentalById(Long id);
    List<RentalDto> getAllRentals();
    RentalDto updateRental(Long id, RentalRequestDto rentalRequestDTO);
    void deleteRental(Long id); // Notez que cette méthode ne retourne rien (void).
}

