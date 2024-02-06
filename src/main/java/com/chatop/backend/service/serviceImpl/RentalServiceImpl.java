package com.chatop.backend.service.serviceImpl;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDto;
import com.chatop.backend.model.Rental;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.RentalRepository;
import com.chatop.backend.repository.UserRepository;
import com.chatop.backend.service.service.RentalService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public RentalServiceImpl(RentalRepository rentalRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RentalDto createRental(RentalRequestDto rentalRequestDTO, Principal principal) {
        User owner = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NoSuchElementException("User not found with email: " + principal.getName()));

        Rental rental = modelMapper.map(rentalRequestDTO, Rental.class);
        rental.setOwner(owner);
        rental.setCreatedAt(new Date());
        rental.setUpdatedAt(new Date());

        Rental savedRental = rentalRepository.save(rental);
        return modelMapper.map(savedRental, RentalDto.class);
    }

    @Override
    public RentalDto getRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found with id: " + id));

        return modelMapper.map(rental, RentalDto.class);
    }

    @Override
    public List<RentalDto> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        return rentals.stream()
                .map(rental -> modelMapper.map(rental, RentalDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RentalDto updateRental(Long id, RentalRequestDto rentalRequestDTO) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found with id: " + id));

        modelMapper.map(rentalRequestDTO, rental);
        rental.setUpdatedAt(new Date());
        Rental updatedRental = rentalRepository.save(rental);

        return modelMapper.map(updatedRental, RentalDto.class);
    }

    @Override
    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }
}

