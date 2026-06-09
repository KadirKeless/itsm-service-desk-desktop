package org.example.ui;

import org.example.dao.DepartmentDAO;
import org.example.dao.RoleDAO;
import org.example.model.Department;
import org.example.model.Role;
import org.example.model.User;
import org.example.service.UserService;
import org.example.utils.ImageUtils;
import org.example.utils.PasswordFieldUtils;
import org.example.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class ProfileUI extends JPanel {

    private User loggedInUser;
    private DashboardUI dashboard;
    private UserService userService;
    private RoleDAO roleDAO;
    private DepartmentDAO departmentDAO;

    private JPasswordField txtCurrentPass;
    private JPasswordField txtNewPass;
    private JPasswordField txtConfirmPass;
    
    private JLabel lblAvatar;

    public ProfileUI(User user, DashboardUI dashboard) {
        this.loggedInUser = user;
        this.dashboard = dashboard;
        this.userService = new UserService();
        this.roleDAO = new RoleDAO();
        this.departmentDAO = new DepartmentDAO();

        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));

        initUI();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(241, 245, 249));
        headerPanel.setBorder(new EmptyBorder(24, 35, 16, 35));

        JButton btnBack = new JButton("← Ana Sayfaya Geri Dön");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setForeground(new Color(37, 99, 235));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> dashboard.goBackOrHome());
        headerPanel.add(btnBack);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblTitle = new JLabel("Profilim");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Kişisel bilgilerinizi görüntüleyebilir ve şifrenizi değiştirebilirsiniz.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(lblSub);
        add(headerPanel, BorderLayout.NORTH);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 11);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 14);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(30, 35, 28, 35)
        ));

        GridBagConstraints g = new GridBagConstraints();

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);

        lblAvatar = new JLabel();
        loadAvatarIcon();
        lblAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(lblAvatar);

        boolean isAdmin = loggedInUser.getRoleId() != null && loggedInUser.getRoleId() == 1;
        if (isAdmin) {
            leftPanel.add(Box.createRigidArea(new Dimension(0, 14)));
            JButton btnChange = buildAvatarButton("✎  Resim Güncelle", new Color(37, 99, 235), Color.WHITE);
            btnChange.addActionListener(e -> uploadPictureAction());
            leftPanel.add(btnChange);

            leftPanel.add(Box.createRigidArea(new Dimension(0, 7)));
            JButton btnRemove = buildAvatarButton("✕  Resmi Kaldır", new Color(254, 226, 226), new Color(185, 28, 28));
            btnRemove.addActionListener(e -> removePictureAction());
            leftPanel.add(btnRemove);
        } else {
            leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            JLabel lblInfo = new JLabel("<html><center>Profil resmi<br>admin tarafından<br>güncellenebilir</center></html>");
            lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            lblInfo.setForeground(new Color(148, 163, 184));
            lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
            leftPanel.add(lblInfo);
        }

        g.gridx = 0; g.gridy = 0;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.insets = new Insets(0, 0, 0, 40);
        g.fill   = GridBagConstraints.NONE;
        cardPanel.add(leftPanel, g);

        String roleName = "Belirtilmemiş";
        if (loggedInUser.getRoleId() != null) {
            Role r = roleDAO.getRoleById(loggedInUser.getRoleId());
            if (r != null) roleName = r.getRoleName();
        }
        String deptName = "Atanmamış";
        if (loggedInUser.getDepartmentId() != null) {
            Department d = departmentDAO.getDepartmentById(loggedInUser.getDepartmentId());
            if (d != null) deptName = d.getDepartmentName();
        }

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);

        rightPanel.add(createDisplayField("AD SOYAD", loggedInUser.getFirstName() + " " + loggedInUser.getLastName(), labelFont, valueFont));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        rightPanel.add(createDisplayField("E-POSTA ADRESİ", loggedInUser.getEmail(), labelFont, valueFont));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        rightPanel.add(createDisplayField("SİSTEM ROLÜ", roleName, labelFont, valueFont));
        rightPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        rightPanel.add(createDisplayField("DEPARTMAN", deptName, labelFont, valueFont));

        g.gridx = 1; g.gridy = 0;
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.insets  = new Insets(0, 0, 0, 0);
        cardPanel.add(rightPanel, g);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(226, 232, 240));
        g.gridx = 0; g.gridy = 1; g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        g.insets = new Insets(26, 0, 22, 0);
        cardPanel.add(sep, g);

        JPanel passTitlePanel = new JPanel();
        passTitlePanel.setLayout(new BoxLayout(passTitlePanel, BoxLayout.Y_AXIS));
        passTitlePanel.setBackground(Color.WHITE);

        JLabel lblPassTitle = new JLabel("Şifre Değiştir");
        lblPassTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPassTitle.setForeground(new Color(30, 41, 59));

        JLabel lblPassReq = new JLabel(ValidationUtils.passwordHint());
        lblPassReq.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPassReq.setForeground(new Color(100, 116, 139));

        passTitlePanel.add(lblPassTitle);
        passTitlePanel.add(Box.createRigidArea(new Dimension(0, 3)));
        passTitlePanel.add(lblPassReq);

        g.gridy = 2; g.insets = new Insets(0, 0, 14, 0);
        cardPanel.add(passTitlePanel, g);

        txtCurrentPass = createPasswordField();
        txtNewPass     = createPasswordField();
        txtConfirmPass = createPasswordField();

        JPanel passRow = new JPanel(new GridLayout(1, 3, 16, 0));
        passRow.setBackground(Color.WHITE);
        passRow.add(createInputGroup("Mevcut Şifreniz", txtCurrentPass, labelFont));
        passRow.add(createInputGroup("Yeni Şifre", txtNewPass, labelFont));
        passRow.add(createInputGroup("Yeni Şifre (Tekrar)", txtConfirmPass, labelFont));

        g.gridy = 3; g.insets = new Insets(0, 0, 0, 0);
        cardPanel.add(passRow, g);

        JLabel lblMessage = new JLabel(" ");
        lblMessage.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton btnUpdatePass = new JButton("Şifreyi Güncelle");
        btnUpdatePass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdatePass.setForeground(Color.WHITE);
        btnUpdatePass.setBackground(new Color(37, 99, 235));
        btnUpdatePass.setFocusPainted(false);
        btnUpdatePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdatePass.setPreferredSize(new Dimension(180, 38));
        btnUpdatePass.addActionListener(e -> updatePasswordAction(lblMessage));

        JPanel btnWrapper = new JPanel();
        btnWrapper.setLayout(new BoxLayout(btnWrapper, BoxLayout.Y_AXIS));
        btnWrapper.setBackground(Color.WHITE);
        btnUpdatePass.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnWrapper.add(Box.createRigidArea(new Dimension(0, 22)));
        btnWrapper.add(btnUpdatePass);
        btnWrapper.add(Box.createRigidArea(new Dimension(0, 8)));
        btnWrapper.add(lblMessage);

        g.gridy = 4; g.insets = new Insets(0, 0, 0, 0);
        cardPanel.add(btnWrapper, g);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(241, 245, 249));
        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.fill = GridBagConstraints.HORIZONTAL;
        cgbc.weightx = 1.0;
        cgbc.anchor  = GridBagConstraints.NORTH;
        cgbc.insets  = new Insets(20, 60, 20, 60);
        centerPanel.add(cardPanel, cgbc);

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton buildAvatarButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(160, 32));
        btn.setPreferredSize(new Dimension(140, 32));
        return btn;
    }

    private JPanel createDisplayField(String labelText, String valueText, Font labelFont, Font valueFont) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(800, 50));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(labelFont);
        lbl.setForeground(new Color(100, 116, 139));
        
        JLabel val = new JLabel(valueText);
        val.setFont(valueFont);
        val.setForeground(new Color(30, 41, 59));

        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(val);
        return panel;
    }

    private JPanel createInputGroup(String labelText, JPasswordField field, Font labelFont) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(800, 96));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(labelFont);
        lbl.setForeground(new Color(71, 85, 105));

        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(field);
        JCheckBox chkShow = PasswordFieldUtils.createShowPasswordCheckbox(field);
        chkShow.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkShow.setBackground(Color.WHITE);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(chkShow);
        return panel;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        pf.putClientProperty("JTextField.placeholderText", ValidationUtils.passwordPlaceholder());
        return pf;
    }

    private void loadAvatarIcon() {
        String init = String.valueOf(loggedInUser.getFirstName().charAt(0)).toUpperCase() +
                      String.valueOf(loggedInUser.getLastName().charAt(0)).toUpperCase();
        ImageIcon icon = ImageUtils.getProfileIcon(loggedInUser.getProfilePicture(), 130, init);
        lblAvatar.setIcon(icon);
    }

    private void uploadPictureAction() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Profil Resmi Seçin");
        chooser.setFileFilter(new FileNameExtensionFilter("Resim Dosyaları (JPG, PNG)", "jpg", "jpeg", "png"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            byte[] bytes = ImageUtils.convertToByteArray(selected, 800);
            if (bytes != null) {
                String err = userService.updateProfilePicture(loggedInUser.getId(), bytes);
                if (err == null) {
                    loggedInUser.setProfilePicture("assets-avatars/avatar_" + loggedInUser.getId() + ".png");
                    loadAvatarIcon();
                    refreshDashboardSidebar();
                } else {
                    JOptionPane.showMessageDialog(this, err, "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Resim okunamadı veya desteklenmeyen format!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removePictureAction() {
        String err = userService.updateProfilePicture(loggedInUser.getId(), null);
        if (err == null) {
            loggedInUser.setProfilePicture(null);
            loadAvatarIcon();
            refreshDashboardSidebar();
        } else {
            JOptionPane.showMessageDialog(this, err, "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshDashboardSidebar() {
        if (dashboard != null) dashboard.refreshSidebarAvatar();
    }

    private void updatePasswordAction(JLabel lblMessage) {
        String currentStr = new String(txtCurrentPass.getPassword());
        String newStr = new String(txtNewPass.getPassword());
        String confStr = new String(txtConfirmPass.getPassword());

        if (currentStr.isEmpty() || newStr.isEmpty() || confStr.isEmpty()) {
            lblMessage.setForeground(new Color(220, 53, 69));
            lblMessage.setText("Lütfen tüm şifre alanlarını doldurunuz!");
            return;
        }

        if (!newStr.equals(confStr)) {
            lblMessage.setForeground(new Color(220, 53, 69));
            lblMessage.setText("Yeni şifreler birbiriyle eşleşmiyor!");
            return;
        }

        String passErr = ValidationUtils.validatePassword(newStr);
        if (passErr != null) {
            lblMessage.setForeground(new Color(220, 53, 69));
            lblMessage.setText(passErr);
            return;
        }

        String error = userService.changePassword(loggedInUser.getId(), currentStr, newStr);
        if (error == null) {
            System.out.println("[BAŞARILI] Şifre güncellendi (Kullanıcı ID: " + loggedInUser.getId() + ")");
            txtCurrentPass.setText("");
            txtNewPass.setText("");
            txtConfirmPass.setText("");
            
            lblMessage.setForeground(new Color(22, 163, 74));
            lblMessage.setText("Şifreniz başarıyla güncellendi!");
            
            // mesajı bir süre sonra sil
            Timer timer = new Timer(3000, evt -> lblMessage.setText(""));
            timer.setRepeats(false);
            timer.start();
        } else {
            lblMessage.setForeground(new Color(220, 53, 69));
            lblMessage.setText(error);
        }
    }
}
