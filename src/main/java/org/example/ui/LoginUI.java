package org.example.ui;

import org.example.model.User;
import org.example.service.UserService;
import org.example.utils.PasswordFieldUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginUI extends JFrame {

    private UserService userService;
    private JTextField txtEmail;
    private JPasswordField txtPassword;

    public LoginUI() {
        this.userService = new UserService();

        setTitle("ITSM — Giriş Yap");
        setSize(460, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ortada kart
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(241, 245, 249));
        setContentPane(wrapper);

        // sabit geniş kart
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(30, 40, 25, 40)
        ));

        // başlık
        JLabel lblTitle = new JLabel("Sisteme Giriş");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Devam etmek için bilgilerinizi giriniz");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(100, 116, 139));
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubtitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblSubtitle);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        // form
        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        txtEmail = new JTextField(20);
        txtEmail.setFont(inputFont);
        txtEmail.putClientProperty("JTextField.placeholderText", "ornek@sirket.com");
        addFormField(card, "E-posta Adresi", txtEmail);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(inputFont);
        txtPassword.putClientProperty("JTextField.placeholderText", "••••••••");
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
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        card.add(Box.createRigidArea(new Dimension(0, 15)));

        // butonlar
        JButton btnLogin = new JButton("Giriş Yap");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(37, 99, 235));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.addActionListener(e -> loginAction());
        card.add(btnLogin);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnGoToRegister = new JButton("<html>Hesabınız yok mu? <b>Kayıt Olun</b></html>");
        btnGoToRegister.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnGoToRegister.setForeground(new Color(37, 99, 235));
        btnGoToRegister.setBorderPainted(false);
        btnGoToRegister.setContentAreaFilled(false);
        btnGoToRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGoToRegister.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGoToRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        btnGoToRegister.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new RegisterUI().setVisible(true));
        });
        card.add(btnGoToRegister);

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
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
    }

    private void loginAction() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Lütfen e-posta ve şifrenizi giriniz!",
                    "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User loggedInUser = userService.login(email, password);

        if (loggedInUser == null) {
            JOptionPane.showMessageDialog(this,
                    "E-posta adresi veya şifre hatalı!",
                    "Giriş Başarısız", JOptionPane.ERROR_MESSAGE);
        } else if (!loggedInUser.isApproved()) {
            boolean isFrozen = loggedInUser.getRoleId() != null && loggedInUser.getDepartmentId() != null;
            if (isFrozen) {
                JOptionPane.showMessageDialog(this,
                        "Hesabınız bir yönetici tarafından dondurulmuştur.\nErişim için sistem yöneticisiyle iletişime geçiniz.",
                        "Hesap Donduruldu", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Hesabınız henüz Admin tarafından onaylanmamış.\nLütfen daha sonra tekrar deneyiniz.",
                        "Onay Bekleniyor", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            dispose();
            new DashboardUI(loggedInUser).setVisible(true);
        }
    }
}