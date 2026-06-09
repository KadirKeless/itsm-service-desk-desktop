package org.example;

import com.formdev.flatlaf.FlatLightLaf;
import org.example.ui.LoginUI;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        FlatLightLaf.setup();

        // genel yuvarlak köşe + font
        UIManager.put("Button.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginUI loginScreen = new LoginUI();
                loginScreen.setVisible(true);
            }
        });
    }
}