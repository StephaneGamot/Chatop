package com.chatop.backend.controller;

import com.chatop.backend.dto.RentalDto;
import com.chatop.backend.dto.RentalRequestDto;
import com.chatop.backend.service.service.RentalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping(value = "")
    @Operation(summary = "Create a new rental",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rental created successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "SuccessResponse", value = "{\"message\": \"Rental created !\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UnauthorizedError", value = "{\"error\": \"Unauthorized access.\"}"))),

            })
    public ResponseEntity<Object> createRental(@ModelAttribute RentalRequestDto rentalRequestDto, Principal principal) {
        try {
            rentalService.createRental(rentalRequestDto, principal);
            return new ResponseEntity<>(Collections.singletonMap("message", "Rental created !"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Error while creating rental: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get a rental by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rental found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RentalDto.class),
                                    examples = @ExampleObject(name = "SuccessResponse", value = "..."))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UnauthorizedError", value = "{\"error\": \"Unauthorized access.\"}"))),
                    @ApiResponse(responseCode = "404", description = "Rental not found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "NotFoundError", value = "Rental not found")))
            })
    public ResponseEntity<RentalDto> getRental(@PathVariable("id") final Long id) {
        try {
            RentalDto rentalDto = rentalService.getRentalById(id);
            return ResponseEntity.ok(rentalDto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("")
    @Operation(summary = "Get all rentals",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of rentals",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = RentalDto.class)))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UnauthorizedError", value = "{\"error\": \"Unauthorized access.\"}")))
            })
    public ResponseEntity<Map<String, Object>> getAllRentals() {
        List<RentalDto> rentals = rentalService.getAllRentals();
        Map<String, Object> response = new HashMap<>();
        response.put("rentals", rentals);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a rental by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rental updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RentalDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UnauthorizedError", value = "{\"error\": \"Unauthorized access.\"}"))),
                    @ApiResponse(responseCode = "404", description = "Rental not found",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "NotFoundError", value = "{\"error\": \"Rental not found.\"}"))),
            })
    public ResponseEntity<?> updateRental(@PathVariable("id") Long id, @ModelAttribute RentalRequestDto rentalRequestDto) {
        try {
            RentalDto updatedRental = rentalService.updateRental(id, rentalRequestDto);
            return ResponseEntity.ok(updatedRental);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Error while updating rental: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Si vous avez une méthode pour supprimer des locations, elle devrait ressembler à cela :
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a rental by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rental deleted successfully",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "SuccessResponse", value = "{\"message\": \"Rental deleted successfully!\"}"))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    examples = @ExampleObject(name = "UnauthorizedError", value = "{\"error\": \"Unauthorized access.\"}")))
            })
    public ResponseEntity<?> deleteRental(@PathVariable Long id) {
        try {
            rentalService.deleteRental(id);
            return ResponseEntity.ok(Collections.singletonMap("message", "Rental deleted successfully!"));
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Error while deleting rental: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }}

