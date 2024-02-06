package com.chatop.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String message;
    private Date createdAt;
    private Date updatedAt;
    private Long user_id;
    private Long rental_id;
}
