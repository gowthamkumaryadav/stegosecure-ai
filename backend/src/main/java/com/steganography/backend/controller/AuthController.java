package com.steganography.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.steganography.backend.model.User;
import com.steganography.backend.repository.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000") // ✅ FIX CORS
public class AuthController {

    @Autowired
    private UserRepository repo;

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        repo.save(user);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User registered"
        ));
    }

    // ✅ LOGIN (ADMIN + EMAIL SUPPORT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        String input = user.getUsername(); // email or username
        String password = user.getPassword();

        System.out.println("🔐 Login attempt: " + input);

        // 🔥 CHECK BOTH EMAIL & USERNAME
        User existing = repo.findByEmail(input);

        if (existing == null) {
            existing = repo.findByUsername(input);
        }

        if (existing != null && existing.getPassword() != null
                && existing.getPassword().equals(password)) {

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Login Success",
                    "username", existing.getEmail() != null
                            ? existing.getEmail()
                            : existing.getUsername()
            ));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "status", "error",
                "message", "Invalid credentials"
        ));
    }

    // 🔥 GOOGLE LOGIN (AUTO CREATE USER)
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {

        String email = body.get("email");

        System.out.println("🌐 Google login: " + email);

        User user = repo.findByEmail(email);

        if (user == null) {
            System.out.println("⚡ Creating new Google user");

            user = new User();
            user.setEmail(email);
            user.setUsername(email); // keep consistent
            user.setPassword(null);  // no password for Google

            repo.save(user);
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Google login success",
                "username", user.getEmail()
        ));
    }
}