package com.steganography.backend.repository;

import com.steganography.backend.model.StegoData;
import com.steganography.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StegoRepository extends JpaRepository<StegoData, Long> {

    long countByTypeAndUser(String type, User user);

    List<StegoData> findByUser(User user);

    List<StegoData> findTop10ByUserOrderByIdDesc(User user);
}