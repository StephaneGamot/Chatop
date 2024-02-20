package com.chatop.backend.configuration;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;  // Source de configuration pour les règles CORS
    private final String base64Secret;                              // La clé secrète pour encoder/decoder les JWT en Base64.

    // Injection des dépendances via le constructeur
    public SpringSecurityConfig(CorsConfigurationSource corsConfigurationSource, @Value("${JWT_SECRET}") String base64Secret) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.base64Secret = base64Secret;
    }

    // Bean définissant la chaîne de filtres de sécurité pour les requêtes HTTP.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource));                                  // Configuration des règles CORS avec la source fournie.
        http.csrf(csrf -> csrf.disable())                                                                      // Désactivation de la protection CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Définit la politique de création de session à 'STATELESS' pour ne pas conserver d'état côté serveur.
                .authorizeHttpRequests(auth -> auth                                                            // Configuration des autorisations de requêtes : quelles requêtes sont autorisées ou nécessitent une authentification.
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/swagger-ui/**", "/v3/api-docs/**", "/images/**").permitAll()   // Permettre à tout le monde d'accéder aux endpoints
                        .anyRequest().authenticated());                                                        // Toutes les autres requêtes doivent être authentifiées.
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));                     // Configuration pour utiliser les tokens JWT comme moyen d'authentification OAuth2.
        return http.build();
    }

    // Bean pour obtenir un gestionnaire d'authentification à partir de la configuration d'authentification.
    // issu de Spring Security

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    // Bean pour encoder les JWT en utilisant la clé secrète.
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(this.base64Secret.getBytes()));
    }


    // Bean pour décoder les JWT, en spécifiant la clé secrète et l'algorithme de hachage.
    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(this.base64Secret.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    // Bean pour encoder les mots de passe avec l'algorithme BCrypt. il va le Hacher + salé ce qui apporte un +++
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}


