package com.chatop.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Entity                   // est une annotation qui indique que la classe correspond à une table de la base de données.
@Table(name = "MESSAGES") // indique le nom de la table associée.
@Data                     // Génère automatiquement les getters, setters, et d'autres méthodes.
@NoArgsConstructor        // Génère un constructeur sans argument.
@AllArgsConstructor       // Génère un constructeur avec un argument pour chaque champ.

public class Message {

    @Id                                                 // Marque ce champ comme la clé primaire de l'entité.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configure la génération automatique de la clé.
    private Long id;                                    // Correspond à `id` dans la table MESSAGES.

    @Column(name = "message", length = 2000)            // Mappe ce champ à la colonne `message`.
    private String message;                             // Correspond à `message` dans la table MESSAGES.

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @ManyToOne                                                 // Indique une relation plusieurs-à-un avec la table USERS.
    @JoinColumn(name = "user_id") // Définit la colonne de jointure et la référence. car nous avons une FOREIGN KEY
    private User user;

    @ManyToOne
    @JoinColumn(name = "rental_id") // Définit la colonne de jointure et la référence. //, referencedColumnName = "id"
    private Rental rental;
}
