package com.chatop.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;         // jwtEncoder est un composant de Spring qui créer des JWT

    public JwtService(JwtEncoder jwtEncoder) {   // Constructeur de la classe. Spring injecte automatiquement un 'jwtEncoder' quand un JwtService est créé.
        this.jwtEncoder = jwtEncoder;
    }

    // Méthode pour générer un jeton JWT à partir des informations d'authentification fournies par Spring Security.
    public String generateToken(Authentication authentication) {
        JwtClaimsSet claims = JwtClaimsSet.builder()                              // Création d'un ensemble de revendications JWT, qui est comme un ensemble d'informations incluses dans le jeton.
                .issuer("self")                                                   // Identifie qui a créé le jeton.
                .subject(authentication.getName())                                // Identifie pour qui le jeton est destiné (l'utilisateur).
                .issuedAt(Instant.now())                                          // La date et l'heure de création du jeton.
                .expiresAt(Instant.now().plus(1, ChronoUnit.DAYS))   // La date et l'heure d'expiration du jeton.
                .build();                                                        // Finalise la construction de l'ensemble des revendications.

        // Paramètres pour encoder le jeton, incluant l'en-tête et les revendications.
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),                      // L'en-tête du jeton qui indique l'algorithme de signature.
                claims);                                                         // Les revendications définies plus haut.

        return this.jwtEncoder.encode(jwtEncoderParameters).getTokenValue();     // Encode le jeton et renvoie sa valeur sous forme de chaîne de caractères.
    }
}
