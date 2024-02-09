package com.chatop.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RentalRequestDto {
    private Long id;

    @NotBlank(message = "Le nom ne peut pas être vide")
    private String name;

    @NotNull(message = "La surface ne peut pas être null")
    private Integer surface;

    @NotNull(message = "Le prix ne peut pas être null")
    private Integer price;

    private MultipartFile picture;

    @NotBlank(message = "La description ne peut pas être vide")
    private String description;

    @NotNull(message = "L'identifiant du propriétaire ne peut pas être null")
    private Long owner_id;

    private String pictureUrl;
}
