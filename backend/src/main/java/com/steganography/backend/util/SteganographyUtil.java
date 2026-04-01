package com.steganography.backend.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SteganographyUtil {

    public static void encode(String inputPath, String outputPath, String message) throws Exception {
        BufferedImage image = ImageIO.read(new File(inputPath));
        byte[] msg = (message + "###END###").getBytes();

        int msgIndex = 0, bitIndex = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int pixel = image.getRGB(x, y);

                if (msgIndex < msg.length) {
                    int bit = (msg[msgIndex] >> (7 - bitIndex)) & 1;
                    pixel = (pixel & 0xFFFFFFFE) | bit;

                    bitIndex++;
                    if (bitIndex == 8) {
                        bitIndex = 0;
                        msgIndex++;
                    }
                }

                image.setRGB(x, y, pixel);
            }
        }

        ImageIO.write(image, "png", new File(outputPath));
    }

    public static String decode(String imagePath) throws Exception {
        BufferedImage image = ImageIO.read(new File(imagePath));
        StringBuilder message = new StringBuilder();

        int bitCount = 0;
        int currentByte = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                int pixel = image.getRGB(x, y);
                int bit = pixel & 1;

                currentByte = (currentByte << 1) | bit;
                bitCount++;

                if (bitCount == 8) {
                    message.append((char) currentByte);
                    bitCount = 0;
                    currentByte = 0;
                }
            }
        }

        String result = message.toString();

     // 🔥 FIX: Check delimiter exists
     int endIndex = result.indexOf("###END###");

     if (endIndex == -1) {
         return null; // or return "" safely
     }

     return result.substring(0, endIndex);
    }
}