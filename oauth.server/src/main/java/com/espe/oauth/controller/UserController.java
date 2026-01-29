package com.espe.oauth.controller;

import com.espe.oauth.dto.CreateUserRequest;
import com.espe.oauth.dto.UserDTO;
import com.espe.oauth.entity.User;
import com.espe.oauth.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de usuarios.
 * Solo accesible por administradores.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Listar todos los usuarios
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Obteniendo lista de usuarios");
        List<UserDTO> users = userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Obtener un usuario por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(convertToDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crear un nuevo usuario
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creando usuario: {}", request.getUsername());

        // Verificar si el usuario ya existe
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El nombre de usuario ya existe"));
        }

        // Validar que se proporcione contraseña para nuevo usuario
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña es requerida para crear un usuario"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setEnabled(request.getEnabled() != null ? request.getEnabled() : true);

        User savedUser = userRepository.save(user);
        log.info("✅ Usuario creado: {}", savedUser.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedUser));
    }

    /**
     * Actualizar un usuario existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest request) {
        log.info("Actualizando usuario ID: {}", id);

        return userRepository.findById(id)
                .map(user -> {
                    // Verificar si el nuevo username ya existe (y no es el mismo usuario)
                    if (!user.getUsername().equals(request.getUsername()) &&
                            userRepository.findByUsername(request.getUsername()).isPresent()) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "El nombre de usuario ya existe"));
                    }

                    user.setUsername(request.getUsername());
                    user.setRole(request.getRole());

                    if (request.getEnabled() != null) {
                        user.setEnabled(request.getEnabled());
                    }

                    // Solo actualizar contraseña si se proporciona una nueva
                    if (request.getPassword() != null && !request.getPassword().isBlank()) {
                        user.setPassword(passwordEncoder.encode(request.getPassword()));
                    }

                    User savedUser = userRepository.save(user);
                    log.info("✅ Usuario actualizado: {}", savedUser.getUsername());
                    return ResponseEntity.ok(convertToDTO(savedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Eliminar un usuario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("Eliminando usuario ID: {}", id);

        return userRepository.findById(id)
                .map(user -> {
                    // Prevenir eliminación del propio admin (protección básica)
                    if (user.getUsername().equals("admin")) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "No se puede eliminar el usuario admin principal"));
                    }

                    userRepository.delete(user);
                    log.info("✅ Usuario eliminado: {}", user.getUsername());
                    return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cambiar estado enabled/disabled de un usuario
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        log.info("Cambiando estado de usuario ID: {}", id);

        return userRepository.findById(id)
                .map(user -> {
                    user.setEnabled(!user.isEnabled());
                    User savedUser = userRepository.save(user);
                    log.info("✅ Estado de usuario '{}' cambiado a: {}",
                            savedUser.getUsername(), savedUser.isEnabled() ? "activo" : "inactivo");
                    return ResponseEntity.ok(convertToDTO(savedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Convierte entidad User a DTO (sin contraseña)
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.isEnabled());
    }
}
