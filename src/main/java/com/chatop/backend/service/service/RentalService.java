package com.chatop.backend.service.service;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDto;
import com.chatop.backend.model.Rental;

import java.util.List;

public interface RentalService {
    List<RentalDto> getAllRentals();

    Rental getRentalById(Long id);

    RentalDto createRental(String name, int surface, int price, String picture, String description, Long owner_id);

    RentalDto updateRental(Long id, RentalRequestDto rentalRequestDTO);

}
