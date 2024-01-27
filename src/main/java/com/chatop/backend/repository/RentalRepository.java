package com.chatop.backend.repository;

import com.chatop.backend.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
}

// Les méthodes CRUD de base sont déjà fournies par JpaRepository