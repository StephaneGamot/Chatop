package com.chatop.backend.service;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDTO;
import com.chatop.backend.model.Rental;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

public interface RentalService {
    List<RentalDto> getAllRentals();
    Rental getRentalById(Long id);
    RentalDto createRental(String name, int surface, int price, MultipartFile picture, String description, Long owner_id) throws IOException;
    RentalDto updateRental(Long id, RentalRequestDTO rentalRequestDTO) throws IOException;
}

