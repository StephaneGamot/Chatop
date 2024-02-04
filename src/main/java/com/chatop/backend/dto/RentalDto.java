package com.chatop.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDto {
    private Long id;
    private String name;
    private int surface;
    private int price;
    private String picture;
    private String description;
    private Long ownerId;
    private Date createdAt;
    private Date updatedAt;
}
