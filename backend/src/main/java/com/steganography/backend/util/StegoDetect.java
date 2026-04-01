package com.steganography.backend.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class StegoDetect {

    public static boolean hasHiddenData(String path) throws Exception {

        BufferedImage img = ImageIO.read(new File(path));

        if (img == null) {
            throw new RuntimeException("Invalid image!");
        }

        int totalPixels = img.getWidth() * img.getHeight();
        int ones = 0;

        // 🔍 LSB scan
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {

                int pixel = img.getRGB(x, y);

                // 👉 Only take BLUE channel LSB (more accurate)
                int blue = pixel & 0xFF;
                int lsb = blue & 1;

                if (lsb == 1) {
                    ones++;
                }
            }
        }

        double ratio = (double) ones / totalPixels;

        System.out.println("LSB Ratio: " + ratio);

        // ⚠️ Relax threshold (better detection)
        boolean suspiciousPattern = (ratio < 0.40 || ratio > 0.60);

        // 🔐 REAL CHECK (MOST IMPORTANT)
        boolean actualDataFound = false;

        try {
            String hiddenData = SteganographyUtil.decode(path);

            if (hiddenData != null && hiddenData.trim().length() > 3) {
                actualDataFound = true;
                System.out.println("✅ Hidden data detected via decode");
            }

        } catch (Exception e) {
            System.out.println("⚠️ Decode failed (maybe no data)");
        }

        // 🎯 FINAL DECISION (PRIORITIZE REAL DATA)
        if (actualDataFound) {
            return true;
        }

        return suspiciousPattern;
    }
}