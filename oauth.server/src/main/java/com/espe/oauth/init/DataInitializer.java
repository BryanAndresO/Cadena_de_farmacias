package com.espe.oauth.init;

import com.espe.oauth.entity.User;
import com.espe.oauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default users if they don't exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println("✅ Created default admin user: admin / admin123");
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User(
                    "user",
                    passwordEncoder.encode("user123"),
                    "ROLE_USER");
            userRepository.save(user);
            System.out.println("✅ Created default user: user / user123");
        }
    }
}
