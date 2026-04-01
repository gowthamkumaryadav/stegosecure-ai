package com.steganography.backend.service;

import com.steganography.backend.util.AESUtil;
import com.steganography.backend.util.SteganographyUtil;
import com.steganography.backend.repository.StegoRepository;
import com.steganography.backend.model.StegoData;
import com.steganography.backend.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.awt.Graphics2D;



@Service
public class StegoService {

    @Autowired
    private StegoRepository repo;

    // =========================
    // 🔐 ENCODE
    // =========================
    public byte[] encode(MultipartFile file, String message, String password, User user) throws Exception {

        System.out.println("🚀 SERVICE ENCODE STARTED");

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No file uploaded");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new RuntimeException("Message cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }

        // 🔍 Read image
        BufferedImage originalImage = ImageIO.read(file.getInputStream());

        if (originalImage == null) {
            throw new RuntimeException("Unsupported image format");
        }

        // 🔥 Convert ANY image → PNG
        BufferedImage pngImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = pngImage.createGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        // 🗂 Save temp input image
        File inputFile = File.createTempFile("encode_input_", ".png");
        ImageIO.write(pngImage, "png", inputFile);

        try {
            // 🔐 Encrypt message
            String encrypted = AESUtil.encrypt(message, password);
            System.out.println("🔐 ENCRYPTED: " + encrypted);

            // 🧠 Encode using SAME utility as decode
            String outputPath = inputFile.getAbsolutePath() + "_encoded.png";

            SteganographyUtil.encode(inputFile.getAbsolutePath(), outputPath, encrypted);

            // 📥 Read encoded image
            File outputFile = new File(outputPath);
            byte[] encodedBytes = Files.readAllBytes(outputFile.toPath());

            // 💾 Save encode history
            if (user != null) {
                StegoData data = new StegoData();
                data.setFileName("encoded_" + System.currentTimeMillis() + ".png");
                data.setMessage(message);
                data.setType("ENCODE");
                data.setUser(user);
                data.setTimestamp(LocalDateTime.now());

                System.out.println("💾 SAVING ENCODE TO DB");
                repo.save(data);
            } else {
                System.out.println("⚠️ USER IS NULL — SKIPPING SAVE");
            }

            return encodedBytes;

        } finally {
            // 🧹 Cleanup temp files
            if (inputFile.exists()) inputFile.delete();
        }
    }

    // =========================
    // 🔓 DECODE
    // =========================
    public String decode(MultipartFile file, String password, User user) throws Exception {

        System.out.println("🚀 SERVICE DECODE STARTED");

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("No file uploaded");
        }

        BufferedImage image = ImageIO.read(file.getInputStream());

        if (image == null) {
            throw new RuntimeException("Invalid image file (unsupported format)");
        }

        // 🔥 Always convert to PNG (important)
        File inputFile = File.createTempFile("decode_", ".png");
        ImageIO.write(image, "png", inputFile);

        try {
            // 🔍 Extract hidden encrypted text
            String encrypted = SteganographyUtil.decode(inputFile.getAbsolutePath());
            System.out.println("📦 EXTRACTED DATA: " + encrypted);

            if (encrypted == null || encrypted.trim().isEmpty()) {
                throw new RuntimeException("No hidden data found in image!");
            }

            String message;

            try {
                // 🔐 Decrypt using password
                message = AESUtil.decrypt(encrypted.trim(), password);
                System.out.println("🔓 DECRYPTED MESSAGE: " + message);
            } catch (Exception e) {
                System.out.println("❌ WRONG PASSWORD");
                throw new RuntimeException("Wrong password!");
            }

            // 💾 Save decode history (optional safety check)
            if (user != null) {
                StegoData data = new StegoData();
                data.setFileName("decoded_" + System.currentTimeMillis() + ".png");
                data.setMessage(message);
                data.setType("DECODE");
                data.setUser(user);
                data.setTimestamp(LocalDateTime.now());

                System.out.println("💾 SAVING DECODE TO DB");
                repo.save(data);
            } else {
                System.out.println("⚠️ USER IS NULL — SKIPPING SAVE");
            }

            return message;

        } finally {
            // 🧹 Always delete temp file
            if (inputFile.exists()) {
                inputFile.delete();
            }
        }
    }

    // =========================
    // 📊 HISTORY
    // =========================
    public List<StegoData> getAll() {
        return repo.findAll();
    }

    // =========================
    // 🔍 DETECT
    // =========================
    public boolean detect(MultipartFile file) throws Exception {

        File temp = File.createTempFile("detect", ".png");
        file.transferTo(temp);

        String data = SteganographyUtil.decode(temp.getAbsolutePath());

        temp.delete();

        return data != null && !data.isEmpty();
    }
    public long getEncodedCount(User user) {
        return repo.countByTypeAndUser("ENCODE", user);
    }

    public long getDecodedCount(User user) {
        return repo.countByTypeAndUser("DECODE", user);
    }

    public List<StegoData> getRecent(User user) {
        return repo.findTop10ByUserOrderByIdDesc(user);
    }
    public List<StegoData> getByUser(User user) {
        return repo.findByUser(user);
    }

	public Object datacleardata(User user) {
		// TODO Auto-generated method stub
		return null;
	}
}
