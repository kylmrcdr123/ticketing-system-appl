package com.rocs.ticketing.system.utils.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Setting up CORS (Cross-Origin Resource Sharing) mappings.
 */
@Configuration
public class WebConfig {
    /**
     * Provides a WebMvcConfigurer bean to configure CORS settings.
     *
     * @return WebMvcConfigurer instance with CORS settings
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/***")
                        .allowedOrigins("http://localhost:3000", "http://192.168.1.39:8080", "https://9875-1-37-88-221.ngrok-free.app")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
