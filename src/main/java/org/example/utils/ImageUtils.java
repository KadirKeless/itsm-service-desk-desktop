package org.example.utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    // dosya varsa yükle, yoksa baş harf avatarı; boyut 2x çizilip küçültülüyor (ekran ölçeği için)
    public static ImageIcon getProfileIcon(String imagePath, int size, String initials) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File imgFile = new File(imagePath);
                if (imgFile.exists()) {
                    BufferedImage original = ImageIO.read(imgFile);
                    if (original != null) {
                        BufferedImage hiRes = buildCircularImage(original, size * 2);
                        return hiDPIIcon(hiRes, size);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return createFallbackInitialsAvatar(size, initials);
    }

    // kare kırp, gerekirse küçült, png byte
    public static byte[] convertToByteArray(File file, int maxSize) {
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) return null;

            int minDim = Math.min(img.getWidth(), img.getHeight());
            int cx = (img.getWidth() - minDim) / 2;
            int cy = (img.getHeight() - minDim) / 2;
            img = img.getSubimage(cx, cy, minDim, minDim);

            if (minDim > maxSize) {
                img = progressiveScale(img, maxSize);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static BufferedImage buildCircularImage(BufferedImage original, int pixels) {
        int minDim = Math.min(original.getWidth(), original.getHeight());
        int cropX  = (original.getWidth()  - minDim) / 2;
        int cropY  = (original.getHeight() - minDim) / 2;
        BufferedImage cropped = original.getSubimage(cropX, cropY, minDim, minDim);
        BufferedImage scaled  = progressiveScale(cropped, pixels);

        BufferedImage result = new BufferedImage(pixels, pixels, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = result.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setClip(new Ellipse2D.Float(0, 0, pixels, pixels));
        g2d.drawImage(scaled, 0, 0, pixels, pixels, null);
        g2d.dispose();
        return result;
    }

    // layout'a küçük boy yaz, çizerken büyük bitmap kullan (Windows ölçek için)
    private static ImageIcon hiDPIIcon(BufferedImage hiRes, int displaySize) {
        return new ImageIcon(hiRes) {
            @Override public int getIconWidth()  { return displaySize; }
            @Override public int getIconHeight() { return displaySize; }
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g2d.drawImage(getImage(), x, y, displaySize, displaySize, c);
                g2d.dispose();
            }
        };
    }

    // birden fazla adımda küçült, tek seferde sıkıştırma bulanık olmasın diye
    private static BufferedImage progressiveScale(BufferedImage src, int target) {
        int current = src.getWidth();
        BufferedImage result = src;

        while (current > target * 2) {
            current = Math.max(current / 2, target);
            BufferedImage step = new BufferedImage(current, current, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = step.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(result, 0, 0, current, current, null);
            g.dispose();
            result = step;
        }

        BufferedImage finalImg = new BufferedImage(target, target, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = finalImg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,       RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.drawImage(result, 0, 0, target, target, null);
        g2d.dispose();
        return finalImg;
    }

    private static ImageIcon createFallbackInitialsAvatar(int size, String initials) {
        int pixels = size * 2;
        BufferedImage image = new BufferedImage(pixels, pixels, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(59, 130, 246));
        g2d.fill(new Ellipse2D.Float(0, 0, pixels, pixels));

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, pixels / 2));
        FontMetrics fm = g2d.getFontMetrics();
        int sw = fm.stringWidth(initials);
        int sa = fm.getAscent();
        g2d.drawString(initials, pixels / 2 - sw / 2, pixels / 2 + sa / 2 - 3);
        g2d.dispose();

        return hiDPIIcon(image, size);
    }
}
