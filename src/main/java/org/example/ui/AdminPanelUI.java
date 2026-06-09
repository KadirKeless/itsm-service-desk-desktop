package org.example.ui;

import org.example.model.Department;
import org.example.model.Role;
import org.example.model.User;
import org.example.dao.DepartmentDAO;
import org.example.dao.RoleDAO;
import org.example.service.UserService;
import org.example.utils.PasswordFieldUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AdminPanelUI extends JPanel {

    private UserService userService;
    private DepartmentDAO departmentDAO;
    private RoleDAO roleDAO;

    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Role> cmbRole;
    private JComboBox<Department> cmbDepartment;
    private List<User> currentUserList;
    private JButton btnActivate;
    private JButton btnFreeze;
    private JButton btnDelete;
    private JButton btnEdit;

    // true: sadece bekleyenler, false: herkes
    private boolean showOnlyUnapproved;

    public AdminPanelUI(DashboardUI dashboard) {
        this.userService = new UserService();
        this.departmentDAO = new DepartmentDAO();
        this.roleDAO = new RoleDAO();
        this.showOnlyUnapproved = true; // Varsayılan: onay bekleyenler

        setLayout(new BorderLayout(0, 12));
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // üst başlık
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(241, 245, 249));

        JPanel titleColumn = new JPanel();
        titleColumn.setLayout(new BoxLayout(titleColumn, BoxLayout.Y_AXIS));
        titleColumn.setBackground(new Color(241, 245, 249));

        JButton btnBack = new JButton(dashboard.getBackNavigationLabel());
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setForeground(new Color(37, 99, 235));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> dashboard.goBackOrHome());
        titleColumn.add(btnBack);
        titleColumn.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel lblHeader = new JLabel("Kullanıcı Yönetimi");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Kullanıcıları onaylayın, dondurun veya silin");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(241, 245, 249));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(lblHeader);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 3)));
        titlePanel.add(lblSub);

        titleColumn.add(titlePanel);

        // Filtre butonları (sağ üst)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterPanel.setBackground(new Color(241, 245, 249));

        JButton btnShowUnapproved = new JButton("Onay Bekleyenler");
        btnShowUnapproved.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnShowUnapproved.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShowUnapproved.addActionListener(e -> { showOnlyUnapproved = true; loadUsers(); });
        filterPanel.add(btnShowUnapproved);

        JButton btnShowAll = new JButton("Tüm Kullanıcılar");
        btnShowAll.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnShowAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShowAll.addActionListener(e -> { showOnlyUnapproved = false; loadUsers(); });
        filterPanel.add(btnShowAll);

        JButton btnAddUser = new JButton("+ Yeni Kullanıcı");
        btnAddUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddUser.setForeground(Color.WHITE);
        btnAddUser.setBackground(new Color(59, 130, 246));
        btnAddUser.setFocusPainted(false);
        btnAddUser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddUser.addActionListener(e -> showAddUserDialog());
        filterPanel.add(btnAddUser);

        headerPanel.add(titleColumn, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // tablo
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JCheckBox chkSelectAll = new JCheckBox("Tümünü Seç");
        chkSelectAll.setBackground(Color.WHITE);
        chkSelectAll.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkSelectAll.setFocusPainted(false);
        JPanel topTablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        topTablePanel.setBackground(Color.WHITE);
        topTablePanel.add(chkSelectAll);
        tableCard.add(topTablePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Ad", "Soyad", "E-posta", "Rol", "Departman", "Durum"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 250, 252));
        table.getTableHeader().setForeground(new Color(71, 85, 105));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(new Color(30, 41, 59));
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setAutoCreateRowSorter(true);

        // Durum sütunu renklendirme + alternatif satır
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }

                // Durum sütunu (index 6) renklendirme
                if (col == 6 && value != null) {
                    String status = value.toString();
                    if (status.equals("Aktif")) setForeground(new Color(22, 163, 74));
                    else if (status.equals("Dondurulmuş")) setForeground(new Color(220, 53, 69));
                    else if (status.equals("Onay Bekliyor")) setForeground(new Color(217, 119, 6));
                    else setForeground(new Color(30, 41, 59));
                } else if (!isSelected) {
                    setForeground(new Color(30, 41, 59));
                }

                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // Tüm kullanıcılar listesinde çift tıklama → güncelleme modalı
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2 || showOnlyUnapproved) {
                    return;
                }
                int viewRow = table.rowAtPoint(e.getPoint());
                if (viewRow < 0) {
                    return;
                }
                int modelRow = table.convertRowIndexToModel(viewRow);
                User u = currentUserList.get(modelRow);
                String status = (String) tableModel.getValueAt(modelRow, 6);
                if ("Onay Bekliyor".equals(status)) {
                    JOptionPane.showMessageDialog(AdminPanelUI.this,
                            "Onay bekleyen kullanıcı bu pencereden güncellenemez. Önce alttan Aktifleştir ile onaylayınız.",
                            "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (u.getRoleId() != null && u.getRoleId() == 1) {
                    JOptionPane.showMessageDialog(AdminPanelUI.this,
                            "Admin hesabı üzerinde değişiklik yapılamaz!",
                            "Uyarı", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                table.setRowSelectionInterval(viewRow, viewRow);
                openEditUserDialog(u);
            }
        });

        // alt: onay / dondur / sil vb.
        JPanel bottomCard = new JPanel();
        bottomCard.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        bottomCard.setBackground(Color.WHITE);
        bottomCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        Font formFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Onay formu
        bottomCard.add(createLabel("Rol:", formFont));
        cmbRole = new JComboBox<>();
        cmbRole.setFont(formFont);
        cmbRole.addItem(null); // Placeholder
        loadRoles();
        cmbRole.setRenderer(createPlaceholderRenderer("Rol seçiniz..."));
        cmbRole.setEnabled(false); // Varsayılan kapalı
        bottomCard.add(cmbRole);

        bottomCard.add(createLabel("Departman:", formFont));
        cmbDepartment = new JComboBox<>();
        cmbDepartment.setFont(formFont);
        cmbDepartment.addItem(null); // Placeholder
        loadDepartments();
        cmbDepartment.setRenderer(createPlaceholderRenderer("Departman seçiniz..."));
        cmbDepartment.setEnabled(false); // Varsayılan kapalı
        bottomCard.add(cmbDepartment);

        // Aksiyon butonları
        btnActivate = createActionButton("Aktifleştir", new Color(22, 163, 74));
        btnActivate.setEnabled(false);
        btnActivate.addActionListener(e -> activateAction());
        bottomCard.add(btnActivate);

        btnFreeze = createActionButton("Dondur", new Color(217, 119, 6));
        btnFreeze.setEnabled(false);
        btnFreeze.addActionListener(e -> freezeAction());
        bottomCard.add(btnFreeze);

        btnDelete = createActionButton("Sil", new Color(220, 53, 69));
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deleteAction());
        bottomCard.add(btnDelete);

        btnEdit = createActionButton("Güncelle", new Color(99, 102, 241));
        btnEdit.setEnabled(false);
        btnEdit.addActionListener(e -> editUserAction());
        bottomCard.add(btnEdit);

        add(bottomCard, BorderLayout.SOUTH);

        // Tümünü seç checkbox dinleyicisi
        chkSelectAll.addActionListener(e -> {
            boolean isAdjusting = table.getSelectionModel().getValueIsAdjusting();
            table.getSelectionModel().setValueIsAdjusting(true);
            if (chkSelectAll.isSelected()) {
                table.selectAll();
            } else {
                table.clearSelection();
            }
            table.getSelectionModel().setValueIsAdjusting(isAdjusting);
        });

        // Tablo seçim dinleyicisi
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int[] rows = table.getSelectedRows();

                boolean allPending = rows.length > 0;
                boolean allActive  = rows.length > 0;
                boolean allPassive = rows.length > 0;   // Dondurulmuş veya Onay Bekliyor

                for (int viewRow : rows) {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    String status = (String) tableModel.getValueAt(modelRow, 6);
                    if (!"Onay Bekliyor".equals(status)) allPending = false;
                    if (!"Aktif".equals(status))          allActive  = false;
                    if ("Aktif".equals(status))            allPassive = false;
                }

                // Rol/Departman dropdown: sadece tümü "Onay Bekliyor" ise açık
                cmbRole.setEnabled(allPending);
                cmbDepartment.setEnabled(allPending);

                // Aktifleştir: seçili herkesi zaten Aktif ise kilitle
                btnActivate.setEnabled(rows.length > 0 && !allActive);

                // Dondur: seçili herkes zaten pasifse kilitle
                btnFreeze.setEnabled(rows.length > 0 && !allPassive);

                // Sil: en az bir seçim yeterliyken açık
                btnDelete.setEnabled(rows.length > 0);

                // Güncelle: tek seçim VE onaylı (Aktif veya Dondurulmuş) ise açık
                boolean singleApproved = false;
                if (rows.length == 1) {
                    int modelRow = table.convertRowIndexToModel(rows[0]);
                    String status = (String) tableModel.getValueAt(modelRow, 6);
                    singleApproved = !"Onay Bekliyor".equals(status);
                }
                btnEdit.setEnabled(singleApproved);

                // Tümünü Seç tikini dinamik güncelle
                if (table.getRowCount() > 0) {
                    chkSelectAll.setSelected(table.getSelectedRowCount() == table.getRowCount());
                } else {
                    chkSelectAll.setSelected(false);
                }
            }
        });

        loadUsers();
    }

    // tabloyu yenile
    private void loadUsers() {
        tableModel.setRowCount(0);

        if (showOnlyUnapproved) {
            currentUserList = userService.getUnapprovedUsers();
        } else {
            currentUserList = userService.getAllUsers();
        }

        for (User user : currentUserList) {
            String roleName = resolveRoleName(user.getRoleId());
            String deptName = resolveDepartmentName(user.getDepartmentId());
            String status = resolveStatus(user);

            tableModel.addRow(new Object[]{
                    user.getId(), user.getFirstName(), user.getLastName(),
                    user.getEmail(), roleName, deptName, status
            });
        }
    }

    private void loadRoles() {
        List<Role> roles = roleDAO.getAllRoles();
        for (Role role : roles) {
            if (role.getId() != 1) cmbRole.addItem(role);
        }
    }

    private void loadDepartments() {
        List<Department> departments = departmentDAO.getAllDepartments();
        for (Department dept : departments) {
            cmbDepartment.addItem(dept);
        }
    }

    // toplu işlemler — activateUser servise gider
    private void activateAction() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Lütfen işlem yapmak için en az bir kullanıcı seçiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int viewRow : rows) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            String status = (String) tableModel.getValueAt(modelRow, 6);
            if ("Aktif".equals(status)) {
                if (rows.length == 1) {
                    User u = currentUserList.get(modelRow);
                    JOptionPane.showMessageDialog(this,
                            u.getFirstName() + " " + u.getLastName() + " zaten Aktif durumda.\n'Dondurulmuş' veya 'Onay Bekliyor' durumundaki bir hesap seçiniz.",
                            "Hatalı Seçim", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Seçili kullanıcılar arasında zaten Aktif olanlar bulunuyor.\nToplu işlem için lütfen sadece 'Dondurulmuş' veya 'Onay Bekliyor' durumundaki hesapları seçiniz.",
                            "Hatalı Seçim", JOptionPane.WARNING_MESSAGE);
                }
                return;
            }
        }

        Role selectedRole = (Role) cmbRole.getSelectedItem();
        Department selectedDept = (Department) cmbDepartment.getSelectedItem();

        Integer roleId = (selectedRole != null) ? selectedRole.getId() : null;
        Integer deptId = (selectedDept != null) ? selectedDept.getId() : null;

        int successCount = 0;
        int failCount = 0;
        String lastError = null;
        for (int viewRow : rows) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            int userId = (int) tableModel.getValueAt(modelRow, 0);
            String error = userService.activateUser(userId, roleId, deptId);
            if (error == null) successCount++;
            else { failCount++; lastError = error; }
        }

        if (rows.length == 1) {
            if (failCount > 0) {
                User user = currentUserList.get(table.convertRowIndexToModel(rows[0]));
                String name = user.getFirstName() + " " + user.getLastName();
                JOptionPane.showMessageDialog(this, name + " aktifleştirilemedi:\n" + lastError, "Hata", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (failCount > 0) {
                String msg = successCount + " kullanıcı başarıyla aktifleştirildi.\n" + failCount + " kullanıcıda hata oluştu:\n" + lastError;
                JOptionPane.showMessageDialog(this, msg, "Uyarı", JOptionPane.WARNING_MESSAGE);
            }
        }
        loadUsers();
    }

    private void freezeAction() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Lütfen işlem yapmak için en az bir kullanıcı seçiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int viewRow : rows) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            String status = (String) tableModel.getValueAt(modelRow, 6);
            User userObj = currentUserList.get(modelRow);
            
            if (!"Aktif".equals(status)) {
                if (rows.length == 1) {
                    JOptionPane.showMessageDialog(this,
                            userObj.getFirstName() + " " + userObj.getLastName() + " zaten pasif durumda.\nLütfen 'Aktif' bir hesap seçiniz.",
                            "Hatalı Seçim", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Seçili kullanıcılar arasında zaten pasif (dondurulmuş veya onay bekleyen) durumda olanlar var.\nToplu işlem için lütfen SADECE 'Aktif' kullanıcıları seçiniz.",
                            "Hatalı Seçim", JOptionPane.WARNING_MESSAGE);
                }
                return;
            }
            if (userObj.getRoleId() != null && userObj.getRoleId() == 1) {
                JOptionPane.showMessageDialog(this,
                        rows.length == 1
                                ? "Admin hesabı dondurulamaz."
                                : "Seçili kullanıcılar arasında Admin hesabı bulunuyor! Admin hesapları dondurulamaz.",
                        "Kritik Uyarı", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String freezeConfirmMsg;
        if (rows.length == 1) {
            User su = currentUserList.get(table.convertRowIndexToModel(rows[0]));
            freezeConfirmMsg = su.getFirstName() + " " + su.getLastName() + " adlı kullanıcının hesabı dondurulacak.\nBu kullanıcı sisteme giriş yapamayacaktır.\nDevam etmek istiyor musunuz?";
        } else {
            freezeConfirmMsg = "Seçili " + rows.length + " kullanıcının hesabı dondurulacak.\nKullanıcılar sisteme giriş yapamayacaktır.\nDevam etmek istiyor musunuz?";
        }
        int confirm = JOptionPane.showConfirmDialog(this, freezeConfirmMsg,
                "Kullanıcı Dondur", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int successCount = 0;
            int failCount = 0;
            String lastError = null;
            for (int viewRow : rows) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                int userId = (int) tableModel.getValueAt(modelRow, 0);
                String error = userService.freezeUser(userId);
                if (error == null) successCount++;
                else {
                    failCount++;
                    lastError = error;
                }
            }
            
            if (rows.length == 1) {
                if (failCount > 0) {
                    String msg = lastError != null ? lastError : "İşlem gerçekleştirilemedi.";
                    JOptionPane.showMessageDialog(this, msg, "Dondurulamadı", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                if (failCount > 0) {
                    String msg = successCount + " kullanıcı donduruldu.\n" + failCount + " kullanıcıda işlem yapılamadı.";
                    if (lastError != null) {
                        msg += "\n\nSon hata: " + lastError;
                    }
                    JOptionPane.showMessageDialog(this, msg, "Uyarı", JOptionPane.WARNING_MESSAGE);
                }
            }
            loadUsers();
        }
    }

    private void deleteAction() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Lütfen işlem yapmak için en az bir kullanıcı seçiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int viewRow : rows) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            User userObj = currentUserList.get(modelRow);
            if (userObj.getRoleId() != null && userObj.getRoleId() == 1) {
                JOptionPane.showMessageDialog(this,
                        rows.length == 1
                                ? "Admin hesabı sistemden silinemez."
                                : "Seçilen kullanıcılar arasında Admin hesabı bulunuyor! Admin hesapları sistemden silinemez.",
                        "Kritik Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String deleteConfirmMsg;
        if (rows.length == 1) {
            User su = currentUserList.get(table.convertRowIndexToModel(rows[0]));
            deleteConfirmMsg = su.getFirstName() + " " + su.getLastName() + " adlı kullanıcı sistemden tamamen silinecek!\nBu işlem GERİ ALINAMAZ. Devam etmek istiyor musunuz?";
        } else {
            deleteConfirmMsg = "Seçili " + rows.length + " kullanıcı sistemden tamamen silinecek!\nBu işlem GERİ ALINAMAZ. Devam etmek istiyor musunuz?";
        }
        int confirm = JOptionPane.showConfirmDialog(this, deleteConfirmMsg,
                "Kullanıcı Sil", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int successCount = 0;
            int failCount = 0;
            for (int viewRow : rows) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                int userId = (int) tableModel.getValueAt(modelRow, 0);
                String error = userService.deleteUser(userId);
                if (error == null) successCount++;
                else failCount++;
            }
            
            if (rows.length == 1) {
                if (failCount > 0) {
                    User user = currentUserList.get(table.convertRowIndexToModel(rows[0]));
                    String name = user.getFirstName() + " " + user.getLastName();
                    String msg = name + " silinemedi (talebi bulunuyor olabilir).";
                    JOptionPane.showMessageDialog(this, msg, "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (failCount > 0) {
                    String msg = successCount + " kullanıcı başarıyla silindi.\n" + failCount + " kullanıcı silinemedi (mevcut talepleri olduğu için).";
                    JOptionPane.showMessageDialog(this, msg, "Uyarı", JOptionPane.WARNING_MESSAGE);
                }
            }
            loadUsers();
        }
    }

    // kullanıcı düzenle penceresi
    private void editUserAction() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Lütfen tablodan bir kullanıcı seçiniz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (rows.length > 1) {
            JOptionPane.showMessageDialog(this, "Toplu güncelleme yapılamaz! Lütfen sadece tek bir kullanıcı seçiniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(rows[0]);
        User user = currentUserList.get(modelRow);

        if (user.getRoleId() != null && user.getRoleId() == 1) {
            JOptionPane.showMessageDialog(this, "Admin hesabı üzerinde değişiklik yapılamaz!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        openEditUserDialog(user);
    }

    private void openEditUserDialog(User user) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Kullanıcı Düzenle — #" + user.getId(), true);
        dialog.setLayout(new BorderLayout(0, 0));

        Font formFont = new Font("Segoe UI", Font.PLAIN, 13);
        String initials = user.getFirstName().substring(0, 1).toUpperCase() + user.getLastName().substring(0, 1).toUpperCase();

        // avatar
        final byte[][] pendingAvatar = {null};
        final boolean[] removeAvatarRequested = {false};

        JLabel lblAvatarPreview = new JLabel();
        lblAvatarPreview.setIcon(org.example.utils.ImageUtils.getProfileIcon(user.getProfilePicture(), 80, initials));

        JButton btnSelectImage = new JButton("Resim Seç...");
        btnSelectImage.setFont(formFont);
        btnSelectImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSelectImage.setFocusPainted(false);

        JButton btnRemoveImage = new JButton("Resmi Kaldır");
        btnRemoveImage.setFont(formFont);
        btnRemoveImage.setForeground(new Color(185, 28, 28));
        btnRemoveImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRemoveImage.setFocusPainted(false);

        Runnable refreshRemoveButton = () -> {
            boolean hasDbPicture = user.getProfilePicture() != null && !user.getProfilePicture().trim().isEmpty();
            boolean showRemove = (hasDbPicture && !removeAvatarRequested[0]) || pendingAvatar[0] != null;
            btnRemoveImage.setVisible(showRemove);
        };

        btnSelectImage.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Profil Resmi Seçin");
            chooser.setFileFilter(new FileNameExtensionFilter("Resim Dosyaları (JPG, PNG)", "jpg", "jpeg", "png"));
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                byte[] bytes = org.example.utils.ImageUtils.convertToByteArray(chooser.getSelectedFile(), 800);
                if (bytes != null) {
                    pendingAvatar[0] = bytes;
                    removeAvatarRequested[0] = false;
                    ImageIcon preview = new ImageIcon(bytes);
                    Image scaled = preview.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    lblAvatarPreview.setIcon(new ImageIcon(scaled));
                } else {
                    JOptionPane.showMessageDialog(dialog, "Resim okunamadı veya desteklenmeyen format!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
            refreshRemoveButton.run();
        });

        btnRemoveImage.addActionListener(e -> {
            if (pendingAvatar[0] != null) {
                pendingAvatar[0] = null;
                lblAvatarPreview.setIcon(org.example.utils.ImageUtils.getProfileIcon(
                        removeAvatarRequested[0] ? null : user.getProfilePicture(), 80, initials));
            } else {
                removeAvatarRequested[0] = true;
                pendingAvatar[0] = null;
                lblAvatarPreview.setIcon(org.example.utils.ImageUtils.getProfileIcon(null, 80, initials));
            }
            refreshRemoveButton.run();
        });

        JPanel avatarRight = new JPanel();
        avatarRight.setLayout(new BoxLayout(avatarRight, BoxLayout.Y_AXIS));
        avatarRight.setBackground(new Color(248, 250, 252));

        JLabel lblUserName = new JLabel(user.getFirstName() + " " + user.getLastName());
        lblUserName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserName.setForeground(new Color(30, 41, 59));

        JLabel lblAvatarSub = new JLabel("Profil resmini değiştirmek için:");
        lblAvatarSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAvatarSub.setForeground(new Color(100, 116, 139));

        avatarRight.add(lblUserName);
        avatarRight.add(Box.createRigidArea(new Dimension(0, 4)));
        avatarRight.add(lblAvatarSub);
        avatarRight.add(Box.createRigidArea(new Dimension(0, 8)));
        avatarRight.add(btnSelectImage);
        avatarRight.add(Box.createRigidArea(new Dimension(0, 6)));
        avatarRight.add(btnRemoveImage);
        refreshRemoveButton.run();

        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        avatarPanel.setBackground(new Color(248, 250, 252));
        avatarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        avatarPanel.add(lblAvatarPreview);
        avatarPanel.add(avatarRight);

        dialog.add(avatarPanel, BorderLayout.NORTH);

        // form alanları
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(12, 14, 12, 14));

        JTextField txtFirst = new JTextField(user.getFirstName());
        txtFirst.setFont(formFont);
        JTextField txtLast = new JTextField(user.getLastName());
        txtLast.setFont(formFont);
        JTextField txtMail = new JTextField(user.getEmail());
        txtMail.setFont(formFont);

        JComboBox<Role> cmbEditRole = new JComboBox<>();
        cmbEditRole.setFont(formFont);
        cmbEditRole.addItem(null);
        java.util.List<Role> roles = roleDAO.getAllRoles();
        Role currentRole = null;
        for (Role r : roles) {
            if (r.getId() != 1) {
                cmbEditRole.addItem(r);
                if (user.getRoleId() != null && user.getRoleId() == r.getId()) currentRole = r;
            }
        }
        if (currentRole != null) cmbEditRole.setSelectedItem(currentRole);
        cmbEditRole.setRenderer(createPlaceholderRenderer("Seçiniz..."));

        JComboBox<Department> cmbEditDept = new JComboBox<>();
        cmbEditDept.setFont(formFont);
        cmbEditDept.addItem(null);
        java.util.List<Department> depts = departmentDAO.getAllDepartments();
        Department currentDept = null;
        for (Department d : depts) {
            cmbEditDept.addItem(d);
            if (user.getDepartmentId() != null && user.getDepartmentId() == d.getId()) currentDept = d;
        }
        if (currentDept != null) cmbEditDept.setSelectedItem(currentDept);
        cmbEditDept.setRenderer(createPlaceholderRenderer("Seçiniz..."));

        formPanel.add(createLabel("Ad:", formFont));       formPanel.add(txtFirst);
        formPanel.add(createLabel("Soyad:", formFont));    formPanel.add(txtLast);
        formPanel.add(createLabel("E-posta:", formFont));  formPanel.add(txtMail);
        formPanel.add(createLabel("Rol:", formFont));      formPanel.add(cmbEditRole);
        formPanel.add(createLabel("Departman:", formFont)); formPanel.add(cmbEditDept);

        dialog.add(formPanel, BorderLayout.CENTER);

        // kaydet / iptal
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnCancel = new JButton("İptal");
        JButton btnSave = new JButton("Kaydet");
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);

        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            // 1. Profil resmi: kaldır / yeni yükle
            if (removeAvatarRequested[0]) {
                String avatarErr = userService.updateProfilePicture(user.getId(), null);
                if (avatarErr != null) {
                    JOptionPane.showMessageDialog(dialog, avatarErr, "Resim Kaldırılamadı", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                user.setProfilePicture(null);
            } else if (pendingAvatar[0] != null) {
                String avatarErr = userService.updateProfilePicture(user.getId(), pendingAvatar[0]);
                if (avatarErr != null) {
                    JOptionPane.showMessageDialog(dialog, avatarErr, "Resim Kaydedilemedi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                user.setProfilePicture("assets-avatars/avatar_" + user.getId() + ".png");
            }

            // 2. Kullanıcı verilerini kaydet
            Role selRole = (Role) cmbEditRole.getSelectedItem();
            Department selDept = (Department) cmbEditDept.getSelectedItem();
            Integer roleId = (selRole != null) ? selRole.getId() : null;
            Integer deptId  = (selDept != null) ? selDept.getId() : null;

            String error = userService.updateUser(user.getId(),
                    txtFirst.getText().trim(), txtLast.getText().trim(),
                    txtMail.getText().trim(), roleId, deptId);

            if (error == null) {
                dialog.dispose();
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(dialog, error, "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(450, dialog.getPreferredSize().height));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Yeni Kullanıcı Ekle", true);
        dialog.setLayout(new BorderLayout(0, 0));

        Font font = new Font("Segoe UI", Font.PLAIN, 13);
        final byte[][] pendingAvatar = {null};

        // avatar
        JLabel lblAvatarPreview = new JLabel();
        lblAvatarPreview.setIcon(org.example.utils.ImageUtils.getProfileIcon(null, 80, "YK"));

        JButton btnSelectImage = new JButton("Resim Seç...");
        btnSelectImage.setFont(font);
        btnSelectImage.setFocusPainted(false);
        btnSelectImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSelectImage.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Profil Resmi Seçin");
            chooser.setFileFilter(new FileNameExtensionFilter("Resim Dosyaları (JPG, PNG)", "jpg", "jpeg", "png"));
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                byte[] bytes = org.example.utils.ImageUtils.convertToByteArray(chooser.getSelectedFile(), 800);
                if (bytes != null) {
                    pendingAvatar[0] = bytes;
                    ImageIcon preview = new ImageIcon(bytes);
                    Image scaled = preview.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    lblAvatarPreview.setIcon(new ImageIcon(scaled));
                } else {
                    JOptionPane.showMessageDialog(dialog, "Resim okunamadı!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel avatarInfo = new JPanel();
        avatarInfo.setLayout(new BoxLayout(avatarInfo, BoxLayout.Y_AXIS));
        avatarInfo.setBackground(new Color(248, 250, 252));
        JLabel lblAvatarTitle = new JLabel("Profil Resmi (İsteğe Bağlı)");
        lblAvatarTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAvatarTitle.setForeground(new Color(30, 41, 59));
        JLabel lblAvatarHint = new JLabel("Seçmezseniz baş harfler gösterilir.");
        lblAvatarHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAvatarHint.setForeground(new Color(100, 116, 139));
        avatarInfo.add(lblAvatarTitle);
        avatarInfo.add(Box.createRigidArea(new Dimension(0, 4)));
        avatarInfo.add(lblAvatarHint);
        avatarInfo.add(Box.createRigidArea(new Dimension(0, 8)));
        avatarInfo.add(btnSelectImage);

        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        avatarPanel.setBackground(new Color(248, 250, 252));
        avatarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        avatarPanel.add(lblAvatarPreview);
        avatarPanel.add(avatarInfo);
        dialog.add(avatarPanel, BorderLayout.NORTH);

        // form alanları
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(12, 16, 4, 16));

        JTextField txtFirst = buildTextField(font);
        JTextField txtLast  = buildTextField(font);
        JTextField txtMail  = buildTextField(font);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(font);
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtPass.putClientProperty("JTextField.placeholderText", org.example.utils.ValidationUtils.passwordPlaceholder());

        JComboBox<Role> cmbNewRole = new JComboBox<>();
        cmbNewRole.setFont(font);
        cmbNewRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbNewRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbNewRole.addItem(null);
        for (Role r : roleDAO.getAllRoles()) {
            if (r.getId() != 1) cmbNewRole.addItem(r);
        }
        cmbNewRole.setRenderer(createPlaceholderRenderer("Seçiniz..."));

        JComboBox<Department> cmbNewDept = new JComboBox<>();
        cmbNewDept.setFont(font);
        cmbNewDept.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbNewDept.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbNewDept.addItem(null);
        for (Department d : departmentDAO.getAllDepartments()) cmbNewDept.addItem(d);
        cmbNewDept.setRenderer(createPlaceholderRenderer("Seçiniz..."));

        addFormRow(formPanel, "Ad (Zorunlu):",         txtFirst, font);
        addFormRow(formPanel, "Soyad (Zorunlu):",       txtLast,  font);
        addFormRow(formPanel, "E-posta (Zorunlu):",     txtMail,  font);
        addFormRow(formPanel, "Şifre:", txtPass, font);
        JCheckBox chkShowPass = PasswordFieldUtils.createShowPasswordCheckbox(txtPass);
        chkShowPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkShowPass.setOpaque(false);
        chkShowPass.setBackground(Color.WHITE);
        formPanel.add(chkShowPass);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel lblPassHint = new JLabel(org.example.utils.ValidationUtils.passwordHint());
        lblPassHint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblPassHint.setForeground(new Color(100, 116, 139));
        lblPassHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblPassHint);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        addFormRow(formPanel, "Rol:",                    cmbNewRole,  font);
        addFormRow(formPanel, "Departman:",              cmbNewDept,  font);

        JLabel lblWarn = new JLabel("⚠  Yönetici maks 1, Çalışan maks 5 olabilir.");
        lblWarn.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblWarn.setForeground(new Color(217, 119, 6));
        lblWarn.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblWarn);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        dialog.add(formPanel, BorderLayout.CENTER);

        // kaydet / iptal
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton btnCancel = new JButton("İptal");
        JButton btnSave   = new JButton("Kaydet");
        btnSave.setBackground(new Color(37, 99, 235));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            Role selRole       = (Role)       cmbNewRole.getSelectedItem();
            Department selDept = (Department) cmbNewDept.getSelectedItem();
            String pass        = new String(txtPass.getPassword());

            // E-posta ve şifre ön kontrolü
            String emailErr = org.example.utils.ValidationUtils.validateEmail(txtMail.getText().trim());
            if (emailErr != null) {
                JOptionPane.showMessageDialog(dialog, emailErr, "Geçersiz E-posta", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String passErr = org.example.utils.ValidationUtils.validatePassword(pass);
            if (passErr != null) {
                JOptionPane.showMessageDialog(dialog,
                        passErr + "\n\nŞifre gereksinimleri:\n" + org.example.utils.ValidationUtils.passwordHint(),
                        "Geçersiz Şifre", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String error = userService.adminCreateUser(
                    txtFirst.getText().trim(), txtLast.getText().trim(),
                    txtMail.getText().trim(), pass,
                    selRole != null ? selRole.getId() : null,
                    selDept != null ? selDept.getId() : null);

            if (error != null) {
                JOptionPane.showMessageDialog(dialog, error, "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Profil resmi seçildiyse yeni kullanıcının ID'sini bul ve kaydet
            if (pendingAvatar[0] != null) {
                User newUser = userService.getUserByEmail(txtMail.getText().trim());
                if (newUser != null) {
                    userService.updateProfilePicture(newUser.getId(), pendingAvatar[0]);
                }
            }

            dialog.dispose();
            loadUsers();
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setMinimumSize(new Dimension(400, dialog.getPreferredSize().height));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // form satırı: label + field
    private void addFormRow(JPanel panel, String labelText, JComponent field, Font labelFont) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(labelFont);
        lbl.setForeground(new Color(51, 65, 85));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JTextField buildTextField(Font font) {
        JTextField tf = new JTextField();
        tf.setFont(font);
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        return tf;
    }


    private String resolveRoleName(Integer roleId) {
        if (roleId == null) return "—";
        switch (roleId) {
            case 1: return "Admin";
            case 2: return "Yönetici";
            case 3: return "Çalışan";
            default: return "—";
        }
    }

    private String resolveDepartmentName(Integer deptId) {
        if (deptId == null) return "—";
        var dept = departmentDAO.getDepartmentById(deptId);
        return dept != null ? dept.getDepartmentName() : "—";
    }

    private String resolveStatus(User user) {
        if (user.isApproved()) return "Aktif";
        if (user.getRoleId() != null) return "Dondurulmuş"; // Onaylanmış ama sonra dondurulmuş
        return "Onay Bekliyor"; // Hiç onaylanmamış (rol ve departman NULL)
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(new Color(51, 65, 85));
        return lbl;
    }

    // combo'da boş görünsün diye
    private DefaultListCellRenderer createPlaceholderRenderer(String placeholder) {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText(placeholder);
                    setForeground(new Color(148, 163, 184));
                }
                return this;
            }
        };
    }
}
