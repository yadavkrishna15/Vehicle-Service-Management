package com.vsms.service;

import com.vsms.dto.LoginRequest;
import com.vsms.dto.RegisterRequest;
import com.vsms.model.User;
import com.vsms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new customer or admin.
     * Hashes password using BCrypt.
     */
    public User register(RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with email '" + email + "' already exists.");
        }

        String role = req.getRole();
        if (role == null || role.isBlank() || (!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("CUSTOMER"))) {
            role = "CUSTOMER";
        } else {
            role = role.toUpperCase();
        }

        User user = new User();
        user.setName(req.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone().trim());
        user.setRole(role);
        user.setCreatedAt(new Date());

        User saved = userRepository.save(user);
        saved.setPassword(null); // Never return password in response
        return saved;
    }

    /**
     * Login using email and password.
     * Verifies hashed password with BCrypt.
     */
    public User login(LoginRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials: Email not found."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials: Incorrect password.");
        }

        user.setPassword(null); // Clear password before returning
        return user;
    }
}
