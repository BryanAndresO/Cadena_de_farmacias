package com.espe.catalogo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    // SEC-006 FIX: Orígenes permitidos desde variable de entorno
    // En desarrollo: http://localhost:8080,http://localhost:5173
    // En producción: http://tu-ip:8080
    @Value("${cors.allowed.origins:http://localhost:8080,http://localhost:5173}")
    private String allowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        // SEC-006 FIX: Usar orígenes específicos en lugar de "*"
                        .allowedOrigins(allowedOrigins.split(","))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .exposedHeaders("*")
                        .allowCredentials(true) // Ahora puede ser true con orígenes específicos
                        .maxAge(3600);
            }
        };
    }
}
