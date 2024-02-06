package com.chatop.backend.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RentalRequestDto {
    private Long id;
    private String name;
    private Integer surface;
    private Integer price;
    private MultipartFile picture;
    private String description;
    private Long owner_id;
}
