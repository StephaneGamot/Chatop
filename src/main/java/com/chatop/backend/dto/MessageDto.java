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
public class MessageDto {
    private Long id;

    @NotBlank(message = "Le message ne peut pas être vide")
    private String message;

    private Date created_at;
    private Date updated_at;

    @NotNull(message = "L'identifiant de l'utilisateur ne peut pas être null")
    private Long user_id;

    @NotNull(message = "L'identifiant de la location ne peut pas être null")
    private Long rental_id;
}
