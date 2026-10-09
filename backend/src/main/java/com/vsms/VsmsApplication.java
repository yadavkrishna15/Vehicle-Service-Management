package com.vsms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Main Application Class for Vehicle Service Management System (VSMS).
 * Suitable for B.Sc. Computer Science college project.
 */
@SpringBootApplication
public class VsmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(VsmsApplication.class, args);
        System.out.println("=================================================");
        System.out.println(" Vehicle Service Management System (VSMS) Ready ");
        System.out.println(" Backend running on http://localhost:8080       ");
        System.out.println("=================================================");
    }

    /**
     * BCrypt password encoder bean for hashing passwords securely.
     * Note: Full Spring Security web filters are disabled to keep it simple.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
