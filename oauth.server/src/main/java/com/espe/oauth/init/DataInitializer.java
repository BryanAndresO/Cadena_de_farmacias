package com.espe.oauth.init;

import com.espe.oauth.entity.User;
import com.espe.oauth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializa usuarios por defecto en la base de datos.
 * 
 * SEGURIDAD: Las credenciales se leen desde variables de entorno.
 * En producción, configurar:
 * - ADMIN_USERNAME / ADMIN_PASSWORD
 * - USER_USERNAME / USER_PASSWORD
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Credenciales del administrador desde variables de entorno
    @Value("${app.security.admin.username:admin}")
    private String adminUsername;

    @Value("${app.security.admin.password:${ADMIN_PASSWORD:}}")
    private String adminPassword;

    // Credenciales del usuario normal desde variables de entorno
    @Value("${app.security.user.username:user}")
    private String userUsername;

    @Value("${app.security.user.password:${USER_PASSWORD:}}")
    private String userPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        createAdminUserIfNotExists();
        createNormalUserIfNotExists();
    }

    private void createAdminUserIfNotExists() {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            if (adminPassword == null || adminPassword.isBlank()) {
                log.warn("⚠️ ADMIN_PASSWORD no configurada. El usuario admin NO será creado.");
                log.warn("   Configure la variable de entorno ADMIN_PASSWORD para crear el usuario admin.");
                return;
            }

            User admin = new User(
                    adminUsername,
                    passwordEncoder.encode(adminPassword),
                    "ROLE_ADMIN");
            userRepository.save(admin);
            log.info("✅ Usuario admin creado: {}", adminUsername);
        } else {
            log.info("ℹ️ Usuario admin '{}' ya existe, omitiendo creación.", adminUsername);
        }
    }

    private void createNormalUserIfNotExists() {
        if (userRepository.findByUsername(userUsername).isEmpty()) {
            if (userPassword == null || userPassword.isBlank()) {
                log.warn("⚠️ USER_PASSWORD no configurada. El usuario normal NO será creado.");
                log.warn("   Configure la variable de entorno USER_PASSWORD para crear el usuario.");
                return;
            }

            User user = new User(
                    userUsername,
                    passwordEncoder.encode(userPassword),
                    "ROLE_USER");
            userRepository.save(user);
            log.info("✅ Usuario normal creado: {}", userUsername);
        } else {
            log.info("ℹ️ Usuario '{}' ya existe, omitiendo creación.", userUsername);
        }
    }
}
