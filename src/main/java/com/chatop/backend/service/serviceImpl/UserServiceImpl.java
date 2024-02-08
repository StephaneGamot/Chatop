package com.chatop.backend.service.serviceImpl;

import com.chatop.backend.dto.AuthResponseDto;
import com.chatop.backend.dto.AuthLoginDto;
import com.chatop.backend.dto.AuthRegisterDto;
import com.chatop.backend.dto.UserDto;
import com.chatop.backend.model.User;
import com.chatop.backend.repository.UserRepository;
import com.chatop.backend.security.JwtService;
import com.chatop.backend.service.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;
import java.io.IOException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
    }


    /**
     * Convertit une entité User en UserDto.
     * Cette méthode encapsule la logique de transformation d'une entité de base de données User
     * en un objet de transfert de données UserDto. Elle permet une séparation claire entre
     * l'entité de la base de données et le modèle utilisé dans la couche de présentation ou de transfert.
     * Cela offre une flexibilité pour personnaliser les données exposées à l'API consommateur
     * et assure que les modifications dans la structure de l'entité n'affectent pas directement
     * la réponse de l'API, rendant le code plus maintenable et sécurisé.
     *
     * @param user L'entité User à convertir.
     * @return Un objet UserDto contenant les données de l'entité User.
     */
    private UserDto convertToUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName(), user.getCreated_at(), user.getUpdated_at());
    }


    /* Enregistre un nouvel utilisateur, vérifie si l’email existe déjà, encode son mot de passe …
     * puis enregistre sur BDD et retourne UserDto avec les informations de l'utilisateur enregistré.
     */
    @Override
    public UserDto registerUser(AuthRegisterDto registerDto) throws IOException {
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new IOException("Email already exists.");
        }
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setName(registerDto.getName());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setCreated_at(new Date());
        user.setUpdated_at(new Date());
        User savedUser = userRepository.save(user);

        return  modelMapper.map(savedUser, UserDto.class);
    }


    /*
    *  Authentifie un utilisateur en utilisant son email et son mot de passe.
    * Si l'authentification réussit, un token JWT est généré et retourné dans un objet AuthResponseDto
    * */
    @Override
    public AuthResponseDto loginUser(AuthLoginDto authLoginDto)  {
        // Authentifier l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authLoginDto.getEmail(), authLoginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Générer le token JWT pour cet utilisateur
        String jwt = jwtService.generateToken(authentication);

        // Retourner le token dans un AuthResponseDto
        return new AuthResponseDto(jwt);
    }


/*
* On recherche un utilisateur par son ID.
* Si l'utilisateur est trouvé, il est converti en UserDto et retourné.
* Si l'utilisateur n'est pas trouvé, une exception 404 est lancée .
 */
    @Override
    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return convertToUserDto(user);
    }


    /*
    * On récupère tous les utilisateurs de la base de données, les convertit en UserDto,
    * et retourne une liste de ces objets DTO.
    */
    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }


    /*
    *  On recherche un utilisateur par son email et retourne un Optional<User>.
    * Optional est utilisé ici pour traiter le cas où l'utilisateur n'est pas trouvé
    * */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    /*
    * On récupère l'utilisateur actuellement authentifié en utilisant l'objet Authentication.
    * On recherche ensuite cet utilisateur dans la base de données par son email et si on le retrouve,
    * alors on retourne un UserDto contenant ses informations. Sinon une exception est lancée.
    * */
    @Override
    public UserDto getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return convertToUserDto(user);
    }
}

