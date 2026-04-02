package com.steganography.backend.controller;

import com.steganography.backend.service.StegoService;
import com.steganography.backend.model.StegoData;
import com.steganography.backend.repository.UserRepository;
import com.steganography.backend.model.User;
import com.steganography.backend.util.StegoDetect;
import com.steganography.backend.repository.StegoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/stego")
// allow React sfrontend

public class StegoController {

    @Autowired
    private StegoService service;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private StegoRepository stegoRepo;
    
    

    // =========================
    // 🔐 ENCODE API
    // =========================
    @PostMapping(value="/encode" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> encode(
            @RequestParam("image") MultipartFile file,
            @RequestParam("message") String message,
            @RequestParam("password") String password,
            @RequestParam("username") String username
    ) {

        try {
        	System.out.println("✅ HIT ENCODE API");
            System.out.println("🔥 RECEIVED USERNAME: " + username);

            // ✅ Validate inputs early
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded");
            }

            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Message is empty");
            }

            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is empty");
            }

            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username missing");
            }

            // ✅ FIXED USER FETCH (NO DUPLICATE INSERT)
            User user = null;

            if (username.contains("@")) {
                user = userRepo.findByEmail(username);
            } else {
                user = userRepo.findByUsername(username);
            }

            if (user == null) {
                return ResponseEntity.status(400)
                        .body("User not found. Please login again.");
            }

            // ✅ Encode
            byte[] encodedImage = service.encode(file, message.trim(), password.trim(), user);

            if (encodedImage == null) {
                return ResponseEntity.status(500).body("Encoding failed");
            }

            // ✅ Return image
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=encoded.png")
                    .body(encodedImage);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Encoding failed: " + e.getMessage());
        }
    }

    // =========================
    // 🔓 DECODE API
    // =========================
    @PostMapping(value="/decode" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> decode(
            @RequestParam("image") MultipartFile file,
            @RequestParam("password") String password,
            @RequestParam("username") String username
    ) {
        try {
            System.out.println("🔍 DECODE USERNAME: " + username);

            User user = userRepo.findByEmail(username);

            if (user == null) {
                user = userRepo.findByUsername(username);
            }

            if (user == null) {
                // 🔥 AUTO CREATE USER (IMPORTANT)
                user = new User();

                if (username.contains("@")) {
                    user.setEmail(username);
                    user.setUsername(username);
                } else {
                    user.setUsername(username);
                }

                userRepo.save(user);
            }

            String message = service.decode(file, password, user);

            return ResponseEntity.ok(message);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Decode failed");
        }
    }
    // =========================
    // 📊 HISTORY API (Dashboard)
    // =========================
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestParam String username) {

        System.out.println("📥 HISTORY USERNAME: " + username);

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is missing");
        }

        User user = userRepo.findByEmail(username);

        if (user == null) {
            user = userRepo.findByUsername(username);
        }

        if (user == null) {
            // 🔥 AUTO CREATE USER (IMPORTANT)
            user = new User();

            if (username.contains("@")) {
                user.setEmail(username);
                user.setUsername(username);
            } else {
                user.setUsername(username);
            }

            userRepo.save(user);
        }
        List<StegoData> data = service.getByUser(user);

        return ResponseEntity.ok(data);
    }

    // =========================
    // 🔍 OPTIONAL: DETECT HIDDEN DATA
    // =========================
    @PostMapping("/ai-detect")
    public String detectAI(@RequestParam("file") MultipartFile file) throws Exception {

        File temp = File.createTempFile("detect", ".png");
        file.transferTo(temp);

        boolean result = StegoDetect.hasHiddenData(temp.getAbsolutePath());

        temp.delete();

        return result
                ? "🤖 AI: Hidden Data Detected 🔐"
                : "🤖 AI: No Hidden Data Found ❌";
    }
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestParam String username) {

        System.out.println("📊 DASHBOARD USERNAME: " + username);

        // ✅ Validate username
        if (username == null || username.trim().isEmpty() || username.equals("undefined")) {
            return ResponseEntity.badRequest().body("Username is missing or invalid");
        }

        User user = userRepo.findByEmail(username);

        if (user == null) {
            user = userRepo.findByUsername(username);
        }

        if (user == null) {
            // 🔥 AUTO CREATE USER (IMPORTANT)
            user = new User();

            if (username.contains("@")) {
                user.setEmail(username);
                user.setUsername(username);
            } else {
                user.setUsername(username);
            }

            userRepo.save(user);
        }

        System.out.println("✅ USER FOUND: " + user.getEmail());

        Map<String, Long> data = new HashMap<>();

        data.put("encoded", service.getEncodedCount(user));
        data.put("decoded", service.getDecodedCount(user));
        data.put("secure", service.getEncodedCount(user));

        return ResponseEntity.ok(data);
    }
    
}