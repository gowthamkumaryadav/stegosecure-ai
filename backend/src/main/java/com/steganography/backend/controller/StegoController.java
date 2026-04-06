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

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.File;

@RestController
@RequestMapping("/stego")
public class StegoController {

    @Autowired
    private StegoService service;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private StegoRepository stegoRepo;

    // =========================
    // 🔐 COMMON USER FETCH (NEW)
    // =========================
    private User getUser(String username) {

        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        User user = username.contains("@")
                ? userRepo.findByEmail(username)
                : userRepo.findByUsername(username);

        return user;
    }

    // =========================
    // 🔐 ENCODE API
    // =========================
    @PostMapping(value = "/encode", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> encode(
            @RequestParam("image") MultipartFile file,
            @RequestParam("message") String message,
            @RequestParam("password") String password,
            @RequestParam("username") String username
    ) {
        try {
            System.out.println("✅ HIT ENCODE API");
            System.out.println("🔥 RECEIVED USERNAME: " + username);

            // ✅ Validate
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded");
            }

            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Message is empty");
            }

            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is empty");
            }

            User user = getUser(username);

            if (user == null) {
                return ResponseEntity.status(401)
                        .body("User not authenticated. Please login again.");
            }

            byte[] encodedImage = service.encode(file, message.trim(), password.trim(), user);

            if (encodedImage == null) {
                return ResponseEntity.status(500).body("Encoding failed");
            }

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
    @PostMapping(value = "/decode", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> decode(
            @RequestParam("image") MultipartFile file,
            @RequestParam("password") String password,
            @RequestParam("username") String username
    ) {
        try {
            System.out.println("🔍 DECODE USERNAME: " + username);

            User user = getUser(username);

            if (user == null) {
                return ResponseEntity.status(401)
                        .body("User not authenticated");
            }

            String message = service.decode(file, password, user);

            return ResponseEntity.ok(message);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Decode failed");
        }
    }

    // =========================
    // 📊 HISTORY API
    // =========================
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestParam String username) {

        System.out.println("📥 HISTORY USERNAME: " + username);

        User user = getUser(username);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not authenticated");
        }

        List<StegoData> data = service.getByUser(user);

        return ResponseEntity.ok(data);
    }

    // =========================
    // 🤖 AI DETECT
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

    // =========================
    // 📊 DASHBOARD API
    // =========================
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestParam String username) {

        System.out.println("📊 DASHBOARD USERNAME: " + username);

        if (username == null || username.trim().isEmpty() || username.equals("undefined")) {
            return ResponseEntity.badRequest().body("Username is missing or invalid");
        }

        User user = getUser(username);

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("User not authenticated");
        }

        Map<String, Long> data = new HashMap<>();

        data.put("encoded", service.getEncodedCount(user));
        data.put("decoded", service.getDecodedCount(user));
        data.put("secure", service.getEncodedCount(user));

        return ResponseEntity.ok(data);
    }
}