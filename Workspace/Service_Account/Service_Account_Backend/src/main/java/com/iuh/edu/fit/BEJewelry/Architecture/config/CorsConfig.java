package com.iuh.edu.fit.BEJewelry.Architecture.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8102",
            "http://localhost:8103",
            "http://localhost:8104",
            "http://localhost:8105",
            "http://localhost:8106",
            "http://localhost:8107",
            "http://localhost:8108",
            "http://localhost:8109",
            "http://localhost:8201",
            "http://localhost:8202",
            "http://localhost:8203",
            "http://localhost:8204",
            "http://localhost:8205",
            "http://localhost:8206",
            "http://localhost:8207",
            "http://localhost:8208",
            "http://localhost:8209"
            ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "x-no-retry"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
