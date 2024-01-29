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
    RentalDto createRental(RentalRequestDTO rentalRequestDTO, Principal principal) throws IOException;
    Rental updateRental(Long id, Rental rental);
}

