package com.chatop.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegisterDto {
    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le nom ne peut pas être vide")
    private String name;

    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    private String password;
}
