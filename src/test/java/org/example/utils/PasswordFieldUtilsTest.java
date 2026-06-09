package org.example.utils;

import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordFieldUtilsTest {

    @Test
    public void testSetPasswordVisibleShowsPlaintext() {
        JPasswordField field = new JPasswordField();
        PasswordFieldUtils.setPasswordVisible(field, true);
        assertEquals(0, field.getEchoChar());
    }

    @Test
    public void testSetPasswordVisibleHidesWithBullet() {
        JPasswordField field = new JPasswordField();
        PasswordFieldUtils.setPasswordVisible(field, false);
        assertEquals('\u2022', field.getEchoChar());
    }

    @Test
    public void testCreateShowPasswordCheckboxTogglesEcho() {
        JPasswordField field = new JPasswordField();
        JCheckBox chk = PasswordFieldUtils.createShowPasswordCheckbox(field);
        chk.setSelected(true);
        chk.getActionListeners()[0].actionPerformed(null);
        assertEquals(0, field.getEchoChar());
        chk.setSelected(false);
        chk.getActionListeners()[0].actionPerformed(null);
        assertEquals('\u2022', field.getEchoChar());
    }
}
