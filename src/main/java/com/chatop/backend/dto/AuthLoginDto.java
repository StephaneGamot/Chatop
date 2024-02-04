package com.chatop.backend.dto;
import lombok.Data;

@Data
public class AuthLoginDto {
    private String email;
    private String password;
}
