package com.chatop.backend.model;

// Importation des classes nécessaires de la bibliothèque JPA et Lombok.
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;                    // Lombok @Data génère des getters et setters automatiquement.
import lombok.NoArgsConstructor;       // @NoArgsConstructor génère un constructeur sans argument
import lombok.AllArgsConstructor;      // @AllArgsConstructor génère un constructeur avec tous les arguments.
import java.util.Date;

@Entity                                // est une annotation qui indique que la classe correspond à une table de la base de données.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RENTALS")               // indique le nom de la table associée.

public class Rental {

    @Id                                                    // Identifiant unique de l'entité, mappé à la colonne "id" de la table.
    @GeneratedValue(strategy = GenerationType.IDENTITY)    // @GeneratedValue indique que la valeur est générée automatiquement.
    private Long id;

    @Column(name = "name", length = 255)                   //  Champ mappé à la colonne "name" avec une longueur maximale de 255 caractères
    @NotNull(message = "Nom ne peut pas être null")
    private String name;

    @Column(name = "surface")
    private int surface;

    @Column(name = "price")
    private int price;

    @Column(name = "picture", length = 255)
    private String picture;

    @Column(name = "description", length = 5000)
    private String description;

    @ManyToOne                               // Indique une relation plusieurs-à-un avec la table USERS. c'est le FOREIGN KEY
    @JoinColumn(name = "owner_id") // Définit la colonne de jointure. //, referencedColumnName = "id", nullable = false
    private User ownerId;                    // owner Id est un objet de User

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}
