package com.chatop.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Entity                // est une annotation qui indique que la classe correspond à une table de la base de données.
@Table(name = "USERS") // indique le nom de la table associée.
@Data                  //  est une annotation Lombok. Nul besoin d’ajouter les getters et les setters.
@NoArgsConstructor     // Génère un constructeur sans argument.
@AllArgsConstructor    // Génère un constructeur avec un argument pour chaque champ.

public class User {

    @Id                                     // Marque ce champ comme la clé primaire de l'entité.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configure la génération automatique de la clé.
    private Long id;                        // Correspond à `id` dans la table USERS.

    @Column(name = "email", unique = true)   // Mappe ce champ à la colonne `email`
    @NotNull(message = "Email ne peut pas être null")
    private String email;

    @Column(name = "name")
    @NotNull(message = "Nom ne peut pas être null")
    private String name;

    @Column(name = "password")
    @NotNull(message = "Mot de passe ne peut pas être null")
    private String password;

    @Column(name = "created_at")            // L'attribut name dans l'annotation @Column spécifie explicitement le nom de la colonne dans la BDE.
    @Temporal(TemporalType.TIMESTAMP)       //  TIMESTAMP signifie qu'il enregistre la date et l'heure.
    private Date created_at;                 // le champ Java createdAt doit être mappé à la colonne nommée created_at dans la table de base de données. On se doit de respecter ici le CamelCase

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)       // Spécifie le type de la colonne dans la base de données pour stocker les dates
    private Date updated_at;

    @PrePersist                              // Il est appelé juste avant qu'un nouvel utilisateur soit créé, pour enregistrer la date et l'heure de création.
    protected void onCreate() {
        created_at = new Date();
    }

    @PreUpdate                               // Il est appelé juste avant qu'un utilisateur existant soit mis à jour, pour enregistrer la nouvelle date et l'heure de la mise à jour.
    protected void onUpdate() {
        updated_at = new Date();
    }

    @OneToMany(mappedBy = "owner")
    private Set<Rental> rentals;
}
