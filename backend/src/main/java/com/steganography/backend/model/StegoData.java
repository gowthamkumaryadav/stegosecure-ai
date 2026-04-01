package com.steganography.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class StegoData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ FIXED: proper camelCase
    private String fileName;

    private String message;

    // ✅ FIXED: small 't' (important)
    private LocalDateTime timestamp;

    @Column(name = "type")
    private String type;

    @ManyToOne
    private User user;

    // 🔥 GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}