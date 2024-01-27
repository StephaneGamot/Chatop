package com.chatop.backend.service;

import com.chatop.backend.model.Rental;
import com.chatop.backend.repository.RentalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalService {
    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    public Rental createRental(Rental rental) {
        // Logique de création d'une location
        return rentalRepository.save(rental);
    }

    public Rental updateRental(Long id, Rental rental) {
        return rentalRepository.findById(id).map(existingRental -> {
            existingRental.setName(rental.getName());   // Mettre à jour les champs de existingRental avec les valeurs de rental
            existingRental.setSurface(rental.getSurface());    // Répétez pour les autres champs
            return rentalRepository.save(existingRental);
        }).orElse(null); // ou lever une exception personnalisée
    }
}
