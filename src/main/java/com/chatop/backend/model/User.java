package com.chatop.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@Entity                // est une annotation qui indique que la classe correspond à une table de la base de données.
@Table(name = "USERS") // indique le nom de la table associée.
@Data                  //  est une annotation Lombok. Nul besoin d’ajouter les getters et les setters.
@NoArgsConstructor     // Génère un constructeur sans argument.
@AllArgsConstructor    // Génère un constructeur avec un argument pour chaque champ.

public class User {

    @Id                                     // Marque ce champ comme la clé primaire de l'entité.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configure la génération automatique de la clé.
    private Long id;                        // Correspond à `id` dans la table USERS.

    @Column(name = "email", length = 255)   // Mappe ce champ à la colonne `email`
    @NotNull(message = "Email ne peut pas être null")
    private String email;

    @Column(name = "name", length = 255)
    @NotNull(message = "Nom ne peut pas être null")
    private String name;

    @Column(name = "password", length = 255)
    @NotNull(message = "Mot de passe ne peut pas être null")
    private String password;

    @Column(name = "created_at")            // L'attribut name dans l'annotation @Column spécifie explicitement le nom de la colonne dans la BDE.
    private Date createdAt;                 // le champ Java createdAt doit être mappé à la colonne nommée created_at dans la table de base de données. On se doit de respecter ici le CamelCase

    @Column(name = "updated_at")
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
