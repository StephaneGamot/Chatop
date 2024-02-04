package com.chatop.backend.dto;

import lombok.Data;

@Data
public class AuthRegisterDto {
    private String email;
    private String name;
    private String password;
}
