package com.chatop.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data               // Génère les getters, setters, toString, equals, et hashCode
@NoArgsConstructor  // Génère un constructeur sans arguments
@AllArgsConstructor // Génère un constructeur avec tous les arguments
public class ErrorResponse {

    private String message;
    private List<String> details;
}
