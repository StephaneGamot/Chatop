package com.chatop.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDto {
    private Long id;

    @NotBlank(message = "Le nom ne peut pas être vide")
    private String name;

    @NotNull(message = "La surface ne peut pas être null")
    private int surface;

    @NotNull(message = "Le prix ne peut pas être null")
    private int price;

    private String picture;

    @NotBlank(message = "La description ne peut pas être vide")
    private String description;

    @NotNull(message = "L'identifiant du propriétaire ne peut pas être null")
    private Long owner_id;

    private Date created_at;
    private Date updated_at;
}
