package org.example.utils;

import javax.swing.*;
import java.awt.*;

// şifre kutusu + "göster" tik'i
public final class PasswordFieldUtils {

    private PasswordFieldUtils() {
    }

    public static JCheckBox createShowPasswordCheckbox(JPasswordField field) {
        JCheckBox chk = new JCheckBox("Şifreyi göster");
        Font f = field.getFont();
        if (f != null) {
            chk.setFont(f.deriveFont(Math.max(11f, f.getSize2D() - 1f)));
        }
        chk.setOpaque(false);
        chk.setForeground(new Color(71, 85, 105));
        chk.addActionListener(e -> setPasswordVisible(field, chk.isSelected()));
        return chk;
    }

    public static void setPasswordVisible(JPasswordField field, boolean visible) {
        field.setEchoChar(visible ? (char) 0 : '\u2022');
    }
}
