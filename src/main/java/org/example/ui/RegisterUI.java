package org.example.ui;

import org.example.service.UserService;
import org.example.utils.PasswordFieldUtils;
import org.example.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterUI extends JFrame {

    private UserService userService;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtEmail;
    private JPasswordField txtPassword;

    public RegisterUI() {
        this.userService = new UserService();

        setTitle("ITSM — Kayıt Ol");
        setSize(460, 530);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(241, 245, 249));
        setContentPane(wrapper);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(30, 40, 25, 40)
        ));

        JLabel lblTitle = new JLabel("Hesap Oluştur");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Bilgilerinizi girerek sisteme kayıt olun");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubtitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblSubtitle);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        txtFirstName = new JTextField(20);
        txtFirstName.setFont(inputFont);
        txtFirstName.putClientProperty("JTextField.placeholderText", "Adınız");
        addFormField(card, "Ad", txtFirstName);

        txtLastName = new JTextField(20);
        txtLastName.setFont(inputFont);
        txtLastName.putClientProperty("JTextField.placeholderText", "Soyadınız");
        addFormField(card, "Soyad", txtLastName);

        txtEmail = new JTextField(20);
        txtEmail.setFont(inputFont);
        txtEmail.putClientProperty("JTextField.placeholderText", "ornek@sirket.com");
        addFormField(card, "E-posta Adresi", txtEmail);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(inputFont);
        txtPassword.putClientProperty("JTextField.placeholderText", ValidationUtils.passwordPlaceholder());
        JLabel lblPass = new JLabel("Şifre");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblPass.setForeground(new Color(51, 65, 85));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        card.add(lblPass);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(txtPassword);
        JCheckBox chkShowPass = PasswordFieldUtils.createShowPasswordCheckbox(txtPassword);
        chkShowPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(chkShowPass);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel lblPassHint = new JLabel(ValidationUtils.passwordHint());
        lblPassHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPassHint.setForeground(new Color(100, 116, 139));
        lblPassHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPassHint.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        card.add(lblPassHint);
        card.add(Box.createRigidArea(new Dimension(0, 6)));

        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton btnRegister = new JButton("Kayıt Ol");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBackground(new Color(37, 99, 235));
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnRegister.addActionListener(e -> registerAction());
        card.add(btnRegister);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnGoToLogin = new JButton("<html>Zaten hesabınız var mı? <b>Giriş Yapın</b></html>");
        btnGoToLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnGoToLogin.setForeground(new Color(37, 99, 235));
        btnGoToLogin.setBorderPainted(false);
        btnGoToLogin.setContentAreaFilled(false);
        btnGoToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoToLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGoToLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btnGoToLogin.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
        });
        card.add(btnGoToLogin);

        wrapper.add(card);
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(51, 65, 85));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
    }

    private void registerAction() {
        String firstName = txtFirstName.getText().trim();
        String lastName  = txtLastName.getText().trim();
        String email     = txtEmail.getText().trim();
        String password  = new String(txtPassword.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ad ve soyad boş bırakılamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String emailErr = ValidationUtils.validateEmail(email);
        if (emailErr != null) {
            JOptionPane.showMessageDialog(this, emailErr, "Geçersiz E-posta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String passErr = ValidationUtils.validatePassword(password);
        if (passErr != null) {
            JOptionPane.showMessageDialog(this,
                    passErr + "\n\nŞifre gereksinimleri:\n" + ValidationUtils.passwordHint(),
                    "Geçersiz Şifre", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean isSuccess = userService.register(firstName, lastName, email, password);

        if (isSuccess) {
            JOptionPane.showMessageDialog(this,
                    "Kayıt başarılı! Sisteme girebilmek için Admin onayı bekleniyor.",
                    "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            txtFirstName.setText("");
            txtLastName.setText("");
            txtEmail.setText("");
            txtPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Kayıt başarısız! Bu e-posta adresi zaten kullanımda olabilir.",
                    "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}