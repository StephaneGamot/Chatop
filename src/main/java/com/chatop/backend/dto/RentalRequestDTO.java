package com.chatop.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RentalRequestDTO {
    private String name;

    @NotNull(message = "Surface cannot be null")
    @Positive(message = "Surface must be positive")
    private Integer surface;

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be positive")
    private Integer price;

    private MultipartFile picture;
    private String description;

    @NotNull(message = "Owner ID cannot be null")
    private Long ownerId;
}
