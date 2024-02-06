package com.chatop.backend.service.service;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDto;
import com.chatop.backend.model.Rental;

import java.io.IOException;
import java.util.List;

public interface RentalService {
    List<RentalDto> getAllRentals()throws IOException;

    Rental getRentalById(Long id)throws IOException;

    RentalDto createRental(String name, int surface, int price, String picture, String description, Long owner_id)throws IOException;

    RentalDto updateRental(Long id, RentalRequestDto rentalRequestDTO)throws IOException;
    RentalDto deleteRental(Long id) throws IOException;

}
