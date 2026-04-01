package com.steganography.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.steganography.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUsername(String username);
}