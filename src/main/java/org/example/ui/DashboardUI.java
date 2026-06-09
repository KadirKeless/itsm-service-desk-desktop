package org.example.ui;

import org.example.dao.DepartmentDAO;
import org.example.model.Department;
import org.example.model.User;
import org.example.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;

public class DashboardUI extends JFrame {

    private User loggedInUser;
    private JPanel contentPanel;
    private JPanel sidebar;
    private JLabel lblSidebarAvatar;
    // geri için panel stack'i
    private final Deque<JPanel> panelHistory = new ArrayDeque<>();

    private final Color SIDEBAR_BG = new Color(15, 23, 42);
    private final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    private final Color CONTENT_BG = new Color(248, 250, 252);
    private final Color PRIMARY = new Color(59, 130, 246);
    private final Color TEXT_LIGHT = new Color(148, 163, 184);
    private final Color TEXT_WHITE = new Color(241, 245, 249);
    private final Color DANGER = new Color(239, 68, 68);

    public DashboardUI(User user) {
        this.loggedInUser = user;

        setTitle("ITSM — Ana Panel");
        setSize(1050, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CONTENT_BG);
        contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 41, 59)));

        sidebar.add(Box.createRigidArea(new Dimension(0, 35)));

        JLabel lblApp = new JLabel("ITSM");
        lblApp.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblApp.setForeground(PRIMARY);
        lblApp.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblApp.setMaximumSize(new Dimension(250, 30));
        lblApp.setHorizontalAlignment(SwingConstants.CENTER);
        sidebar.add(lblApp);

        JLabel lblAppSub = new JLabel("Service Management");
        lblAppSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAppSub.setForeground(TEXT_LIGHT);
        lblAppSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAppSub.setMaximumSize(new Dimension(250, 18));
        lblAppSub.setHorizontalAlignment(SwingConstants.CENTER);
        sidebar.add(lblAppSub);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        addUserInfoToSidebar();

        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
        addSeparator();
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton btnHome = createMenuButton("🏠 Ana Sayfa");
        btnHome.addActionListener(e -> showWelcomePanel());
        sidebar.add(btnHome);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));

        buildMenuByRole();

        sidebar.add(Box.createVerticalGlue());

        add(sidebar, BorderLayout.WEST);

        showWelcomePanel();
        add(contentPanel, BorderLayout.CENTER);
    }

    private void buildMenuByRole() {
        Integer roleId = loggedInUser.getRoleId();
        if (roleId == null) return;

        switch (roleId) {
            case 1: buildAdminMenu(); break;
            case 2: buildManagerMenu(); break;
            case 3: buildEmployeeMenu(); break;
        }
    }

    private void buildAdminMenu() {
        JPanel pnlAdmin = createAccordionContainer();
        JButton btnUsers = createMenuButton("Kullanıcı Yönetimi");
        btnUsers.addActionListener(e -> showRootPanel(new AdminPanelUI(this)));
        pnlAdmin.add(btnUsers);
        
        JButton btnAll = createMenuButton("Tüm Talepler");
        btnAll.addActionListener(e -> showRootPanel(new TicketListUI(loggedInUser, this, TicketListUI.MODE_ALL)));
        pnlAdmin.add(btnAll);
        
        addAccordionSection("YÖNETİM", pnlAdmin, true);

        // KİŞİSEL
        JPanel pnlPersonal = createAccordionContainer();
        JButton btnNew = createMenuButton("Yeni Talep Aç");
        btnNew.addActionListener(e -> showRootPanel(new CreateTicketUI(loggedInUser, this)));
        pnlPersonal.add(btnNew);

        JButton btnMy = createMenuButton("Taleplerim");
        btnMy.addActionListener(e -> showRootPanel(new TicketListUI(loggedInUser, this, TicketListUI.MODE_MY_TICKETS)));
        pnlPersonal.add(btnMy);
        
        addAccordionSection("KİŞİSEL", pnlPersonal, true);
    }

    private void buildManagerMenu() {
        JPanel pnlDept = createAccordionContainer();
        JButton btnDept = createMenuButton("Departman Talepleri");
        btnDept.addActionListener(e -> showRootPanel(new TicketListUI(loggedInUser, this, TicketListUI.MODE_DEPARTMENT)));
        pnlDept.add(btnDept);
        addAccordionSection("DEPARTMAN", pnlDept, true);

        JPanel pnlPersonal = createAccordionContainer();
        JButton btnNew = createMenuButton("Yeni Talep Aç");
        btnNew.addActionListener(e -> showRootPanel(new CreateTicketUI(loggedInUser, this)));
        pnlPersonal.add(btnNew);

        JButton btnMy = createMenuButton("Taleplerim");
        btnMy.addActionListener(e -> showRootPanel(new TicketListUI(loggedInUser, this, TicketListUI.MODE_MY_TICKETS)));
        pnlPersonal.add(btnMy);
        
        addAccordionSection("KİŞİSEL", pnlPersonal, true);
    }

    private void buildEmployeeMenu() {
        JPanel pnlReq = createAccordionContainer();
        JButton btnNew = createMenuButton("Yeni Talep Aç");
        btnNew.addActionListener(e -> showRootPanel(new CreateTicketUI(loggedInUser, this)));
        pnlReq.add(btnNew);

        JButton btnMy = createMenuButton("Taleplerim");
        btnMy.addActionListener(e -> showRootPanel(new TicketListUI(loggedInUser, this, TicketListUI.MODE_MY_TICKETS)));
        pnlReq.add(btnMy);
        
        JButton btnAssigned = createMenuButton("Atanan Talepler");
        btnAssigned.addActionListener(e -> showRootPanel(new TicketListUI(loggedInUser, this, TicketListUI.MODE_ASSIGNED)));
        pnlReq.add(btnAssigned);
        
        addAccordionSection("KİŞİSEL TALEPLER", pnlReq, true);
    }

    private void pushCurrentToHistory() {
        Component[] comps = contentPanel.getComponents();
        if (comps.length > 0 && comps[0] instanceof JPanel) {
            panelHistory.push((JPanel) comps[0]);
        }
    }

    private void showContentInternal(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // yeni ekran — eskisini stack'e at
    public void showPanel(JPanel panel) {
        pushCurrentToHistory();
        showContentInternal(panel);
    }

    // menü/profil: geçmişi sıfırla; geri ana sayfaya (veya stack’teki üst katmana)
    public void showRootPanel(JPanel panel) {
        panelHistory.clear();
        showContentInternal(panel);
    }

    public void goBackOrHome() {
        if (panelHistory.isEmpty()) {
            showWelcomePanel();
            return;
        }
        JPanel previous = panelHistory.pop();
        showContentInternal(previous);
    }

    // geçmişe dokunmadan içerik değiş (refresh)
    public void replaceContentPanel(JPanel panel) {
        showContentInternal(panel);
    }

    // silince detay pop, listeyi göster
    public void returnToListAfterTicketDeleted(JPanel listPanel) {
        if (!panelHistory.isEmpty()) {
            panelHistory.pop();
        }
        showContentInternal(listPanel);
    }

    public String getBackNavigationLabel() {
        return panelHistory.isEmpty() ? "← Ana Sayfaya Dön" : "← Geri Dön";
    }

    private void showWelcomePanel() {
        panelHistory.clear();
        contentPanel.removeAll();
        String roleName = resolveRoleName();

        JPanel welcomeWrapper = new JPanel(new BorderLayout());
        welcomeWrapper.setBackground(CONTENT_BG);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CONTENT_BG);
        headerPanel.setBorder(new EmptyBorder(50, 60, 40, 60));

        boolean isAdmin = loggedInUser.getRoleId() != null && loggedInUser.getRoleId() == 1;
        String welcomeLine = isAdmin ? "Hoş geldin, Admin!" : "Hoş geldin, " + loggedInUser.getFirstName() + "!";
        JLabel lblHi = new JLabel(welcomeLine);
        lblHi.setFont(new Font("Inter", Font.BOLD, 34));
        lblHi.setForeground(new Color(15, 23, 42)); // slate-900

        JLabel lblRole = new JLabel(roleName + " Paneline Erişiyorsunuz.");
        lblRole.setFont(new Font("Inter", Font.PLAIN, 16));
        lblRole.setForeground(TEXT_LIGHT);

        headerPanel.add(lblHi);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headerPanel.add(lblRole);

        JPanel cardsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 30));
        cardsContainer.setBackground(CONTENT_BG);
        cardsContainer.setBorder(new EmptyBorder(0, 30, 0, 60));

        cardsContainer.add(createActionCard("📝 Yeni Talep", "Departmanlara yeni bir destek veya hizmet talebi iletin.", new Color(59, 130, 246), () -> showPanel(new CreateTicketUI(loggedInUser, this))));
        cardsContainer.add(createActionCard("📂 Taleplerim", "Açmış olduğunuz tüm taleplerin güncel durumlarını takip edin.", new Color(16, 185, 129), () -> showPanel(new TicketListUI(loggedInUser, this, TicketListUI.MODE_MY_TICKETS))));

        Integer roleId = loggedInUser.getRoleId();
        if (roleId != null && roleId == 1) {
            cardsContainer.add(createActionCard("👥 Kullanıcılar", "Sistemdeki kullanıcıları, yetki ve departmanlarını yönetin.", new Color(139, 92, 246), () -> showPanel(new AdminPanelUI(this))));
            cardsContainer.add(createActionCard("🌐 Tüm Talepler", "Uygulamadaki tüm taleplerin genel haritasını ve durumlarını inceleyin.", new Color(245, 158, 11), () -> showPanel(new TicketListUI(loggedInUser, this, TicketListUI.MODE_ALL))));
        } else if (roleId != null && roleId == 2) {
            cardsContainer.add(createActionCard("🏢 Departman İşleri", "Departmanınıza düşen tüm iş taleplerini ve görevleri listeleyin.", new Color(245, 158, 11), () -> showPanel(new TicketListUI(loggedInUser, this, TicketListUI.MODE_DEPARTMENT))));
        } else if (roleId != null && roleId == 3) {
            cardsContainer.add(createActionCard("📌 Bana Atananlar", "Çözümlemeniz için size yönlendirilmiş aktif görevleri görüntüleyin.", new Color(236, 72, 153), () -> showPanel(new TicketListUI(loggedInUser, this, TicketListUI.MODE_ASSIGNED))));
        }

        welcomeWrapper.add(headerPanel, BorderLayout.NORTH);
        welcomeWrapper.add(cardsContainer, BorderLayout.CENTER);

        contentPanel.add(welcomeWrapper, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createActionCard(String title, String desc, Color ringColor, Runnable action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(320, 160));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, ringColor),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                    new EmptyBorder(25, 25, 25, 25)
                )));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Inter", Font.BOLD, 15));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setFont(new Font("Inter", Font.PLAIN, 13));
        txtDesc.setForeground(new Color(100, 116, 139));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setOpaque(false);
        txtDesc.setEditable(false);
        txtDesc.setFocusable(false);
        txtDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        card.add(lblTitle);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(txtDesc);

        java.awt.event.MouseAdapter cardDispatcher = new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(248, 250, 252));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        };

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(cardDispatcher);
        lblTitle.addMouseListener(cardDispatcher);
        txtDesc.addMouseListener(cardDispatcher);

        return card;
    }

    private String resolveRoleName() {
        if (loggedInUser.getRoleId() == null) return "Kullanıcı";
        switch (loggedInUser.getRoleId()) {
            case 1: return "Sistem Yöneticisi";
            case 2: return "Departman Yöneticisi";
            case 3: return "Çalışan";
            default: return "Kullanıcı";
        }
    }

    private void addUserInfoToSidebar() {
        String init = String.valueOf(loggedInUser.getFirstName().charAt(0)).toUpperCase() +
                      String.valueOf(loggedInUser.getLastName().charAt(0)).toUpperCase();

        ImageIcon avatarIcon = ImageUtils.getProfileIcon(loggedInUser.getProfilePicture(), 110, init);
        lblSidebarAvatar = new JLabel(avatarIcon);
        lblSidebarAvatar.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSidebarAvatar.setMaximumSize(new Dimension(250, 120));
        lblSidebarAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblSidebarAvatar.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblName = new JLabel(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
        lblName.setForeground(TEXT_WHITE);
        lblName.setFont(new Font("Inter", Font.BOLD, 15));
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblName.setMaximumSize(new Dimension(250, 25));
        lblName.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblRole = new JLabel(resolveRoleName());
        lblRole.setForeground(TEXT_LIGHT);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblRole.setMaximumSize(new Dimension(250, 20));
        lblRole.setHorizontalAlignment(SwingConstants.CENTER);

        String deptLine = "Departman atanmamış";
        if (loggedInUser.getDepartmentId() != null) {
            Department dep = new DepartmentDAO().getDepartmentById(loggedInUser.getDepartmentId());
            if (dep != null) {
                deptLine = dep.getDepartmentName();
            }
        }
        JLabel lblDept = new JLabel(deptLine);
        lblDept.setForeground(TEXT_LIGHT);
        lblDept.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDept.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDept.setMaximumSize(new Dimension(250, 32));
        lblDept.setHorizontalAlignment(SwingConstants.CENTER);

        sidebar.add(lblSidebarAvatar);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(lblName);
        sidebar.add(Box.createRigidArea(new Dimension(0, 3)));
        sidebar.add(lblRole);
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        sidebar.add(lblDept);

        sidebar.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnRow.setBackground(SIDEBAR_BG);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(250, 36));

        JButton btnProfile = new JButton("👤 Profil");
        btnProfile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnProfile.setForeground(TEXT_LIGHT);
        btnProfile.setBackground(new Color(30, 41, 59));
        btnProfile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                new EmptyBorder(4, 12, 4, 12)));
        btnProfile.setFocusPainted(false);
        btnProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnProfile.addActionListener(e -> showRootPanel(new ProfileUI(loggedInUser, this)));

        JButton btnLogout = new JButton("🚪 Çıkış");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLogout.setForeground(DANGER);
        btnLogout.setBackground(new Color(30, 41, 59));
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                new EmptyBorder(4, 12, 4, 12)));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
        });

        btnRow.add(btnProfile);
        btnRow.add(btnLogout);
        sidebar.add(btnRow);
    }

    public void refreshSidebarAvatar() {
        if (lblSidebarAvatar == null) return;
        String init = String.valueOf(loggedInUser.getFirstName().charAt(0)).toUpperCase() +
                      String.valueOf(loggedInUser.getLastName().charAt(0)).toUpperCase();
        lblSidebarAvatar.setIcon(ImageUtils.getProfileIcon(loggedInUser.getProfilePicture(), 110, init));
        lblSidebarAvatar.revalidate();
        lblSidebarAvatar.repaint();
    }

    private void addAccordionSection(String text, JPanel togglePanel, boolean isClosedByDefault) {
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JToggleButton toggleBtn = new JToggleButton(text + "  ▼");
        toggleBtn.setFont(new Font("Inter", Font.BOLD, 11));
        toggleBtn.setForeground(TEXT_LIGHT);
        toggleBtn.setBackground(SIDEBAR_BG);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        toggleBtn.setMaximumSize(new Dimension(250, 35));
        toggleBtn.setHorizontalAlignment(SwingConstants.LEFT);
        toggleBtn.setBorder(new EmptyBorder(5, 20, 5, 0)); 
        
        sidebar.add(toggleBtn);
        sidebar.add(togglePanel);
        
        if (isClosedByDefault) {
            togglePanel.setVisible(false);
            toggleBtn.setText(text + "  ►");
            toggleBtn.setSelected(false);
        } else {
            togglePanel.setVisible(true);
            toggleBtn.setSelected(true);
        }

        toggleBtn.addActionListener(e -> {
            boolean isSelected = toggleBtn.isSelected();
            togglePanel.setVisible(isSelected);
            toggleBtn.setText(text + (isSelected ? "  ▼" : "  ►"));
            sidebar.revalidate();
            sidebar.repaint();
        });
    }

    private JPanel createAccordionContainer() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(SIDEBAR_BG);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return pnl;
    }

    private void addSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(30, 41, 59));
        sep.setBackground(new Color(30, 41, 59));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(250, 1));
        sidebar.add(sep);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Inter", Font.PLAIN, 13));
        btn.setForeground(new Color(203, 213, 225));
        btn.setBackground(SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(250, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 35, 10, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(SIDEBAR_HOVER);
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(SIDEBAR_BG);
                btn.setForeground(new Color(203, 213, 225));
            }
        });

        return btn;
    }
}