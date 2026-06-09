package org.example.ui;

import org.example.model.Category;
import org.example.model.Department;
import org.example.model.Priority;
import org.example.model.Ticket;
import org.example.model.TicketReply;
import org.example.model.TicketStatus;
import org.example.model.User;
import org.example.dao.*;
import org.example.service.TicketReplyService;
import org.example.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TicketDetailUI extends JPanel {

    private Ticket ticket;
    private User loggedInUser;
    private DashboardUI dashboard;

    private TicketService ticketService;
    private TicketReplyService ticketReplyService;
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private CategoryDAO categoryDAO;
    private PriorityDAO priorityDAO;
    private TicketStatusDAO ticketStatusDAO;

    private JPanel repliesPanel;
    private JTextArea txtReplyMessage;

    // Renk sabitleri
    private static final Color BG = new Color(241, 245, 249);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color TEXT_PRIMARY = new Color(30, 41, 59);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color SUCCESS = new Color(22, 163, 74);
    private static final Color WARNING = new Color(217, 119, 6);
    private static final Color DANGER = new Color(220, 53, 69);

    public TicketDetailUI(Ticket ticket, User loggedInUser, DashboardUI dashboard) {
        this.ticket = ticket;
        this.loggedInUser = loggedInUser;
        this.dashboard = dashboard;
        this.ticketService = new TicketService();
        this.ticketReplyService = new TicketReplyService();
        this.userDAO = new UserDAO();
        this.departmentDAO = new DepartmentDAO();
        this.categoryDAO = new CategoryDAO();
        this.priorityDAO = new PriorityDAO();
        this.ticketStatusDAO = new TicketStatusDAO();

        setLayout(new BorderLayout(0, 12));
        setBackground(BG);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // üst: ticket özeti
        add(buildInfoCard(), BorderLayout.NORTH);

        // orta: yanıtlar
        repliesPanel = new JPanel();
        repliesPanel.setLayout(new BoxLayout(repliesPanel, BoxLayout.Y_AXIS));
        repliesPanel.setBackground(BG);
        repliesPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        loadReplies();

        JScrollPane scrollPane = new JScrollPane(repliesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // alt: yaz / atama
        add(buildBottomPanel(), BorderLayout.SOUTH);
    }

    // özet kartı
    private JPanel buildInfoCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JButton btnBack = new JButton(dashboard.getBackNavigationLabel());
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setForeground(PRIMARY);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> dashboard.goBackOrHome());
        card.add(btnBack);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        String statusName = resolveStatusName(ticket.getStatusId());
        Color statusColor = getStatusColor(statusName);

        JPanel titleRow = new JPanel(new BorderLayout(12, 0));
        titleRow.setBackground(CARD_BG);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String titleText = ticket.getTitle() != null ? ticket.getTitle() : "";
        JTextArea txtTitleDisplay = new JTextArea("#" + ticket.getId() + " — " + titleText);
        txtTitleDisplay.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtTitleDisplay.setForeground(TEXT_PRIMARY);
        txtTitleDisplay.setLineWrap(true);
        txtTitleDisplay.setWrapStyleWord(true);
        txtTitleDisplay.setEditable(false);
        txtTitleDisplay.setOpaque(false);
        txtTitleDisplay.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JScrollPane titleScroll = new JScrollPane(txtTitleDisplay);
        titleScroll.setBorder(BorderFactory.createEmptyBorder());
        titleScroll.setOpaque(false);
        titleScroll.getViewport().setOpaque(false);
        titleScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        titleScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        titleScroll.setPreferredSize(new Dimension(200, 56));
        titleScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel lblStatus = new JLabel("  " + statusName + "  ");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setOpaque(true);
        lblStatus.setBackground(statusColor);
        lblStatus.setVerticalAlignment(SwingConstants.TOP);

        titleRow.add(titleScroll, BorderLayout.CENTER);
        titleRow.add(lblStatus, BorderLayout.EAST);
        card.add(titleRow);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        JPanel gridPanel = new JPanel(new GridLayout(3, 4, 20, 8));
        gridPanel.setBackground(CARD_BG);
        gridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addInfoCell(gridPanel, "Talep Eden", resolveUserNameWithDept(ticket.getRequesterUserId()));
        addInfoCell(gridPanel, "Departman", resolveDepartmentName(ticket.getTargetDepartmentId()));
        addInfoCell(gridPanel, "Kategori", resolveCategoryName(ticket.getCategoryId()));
        addInfoCell(gridPanel, "Öncelik", resolvePriorityName(ticket.getPriorityId()));
        addInfoCell(gridPanel, "Atanan Kişi",
                ticket.getAssignedUserId() != null ? resolveUserName(ticket.getAssignedUserId()) : "Henüz atanmadı");

        addInfoCell(gridPanel, "Oluşturulma",
                ticket.getCreatedAt() != null ? sdf.format(ticket.getCreatedAt()) : "—");

        // işleme alınma = updated_at; yoksa henüz alınmamış
        String updatedStr;
        if (ticket.getUpdatedAt() == null) {
            updatedStr = "—";
        } else if (ticket.getCreatedAt() != null && ticket.getUpdatedAt().equals(ticket.getCreatedAt())) {
            updatedStr = "Henüz işleme alınmadı";
        } else {
            updatedStr = sdf.format(ticket.getUpdatedAt());
        }
        addInfoCell(gridPanel, "İşleme Alınma", updatedStr);

        addInfoCell(gridPanel, "Kapatılma",
                ticket.getClosedAt() != null ? sdf.format(ticket.getClosedAt()) : "—");

        // grid 3x4 — boş kalan hücreleri doldur
        for (int i = 0; i < 4; i++) {
            gridPanel.add(new JLabel(""));
        }

        card.add(gridPanel);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        JLabel lblDescLabel = new JLabel("Açıklama");
        lblDescLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDescLabel.setForeground(TEXT_SECONDARY);
        lblDescLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblDescLabel);
        card.add(Box.createRigidArea(new Dimension(0, 4)));

        String descText = ticket.getDescription() != null ? ticket.getDescription() : "";
        JTextArea txtDesc = new JTextArea(descText);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setEditable(false);
        txtDesc.setBackground(new Color(248, 250, 252));
        txtDesc.setBorder(new EmptyBorder(8, 10, 8, 10));
        txtDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane descScroll = new JScrollPane(txtDesc);
        descScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        descScroll.setBackground(new Color(248, 250, 252));
        descScroll.getViewport().setBackground(new Color(248, 250, 252));
        descScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        descScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        descScroll.setPreferredSize(new Dimension(400, 168));
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 168));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(descScroll);

        return card;
    }

    // yanıt listesini çiz
    private void loadReplies() {
        repliesPanel.removeAll();

        List<TicketReply> replies = ticketReplyService.getRepliesByTicketId(ticket.getId());
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        JLabel lblRepliesHeader = new JLabel("Yanıtlar (" + replies.size() + ")");
        lblRepliesHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblRepliesHeader.setForeground(TEXT_PRIMARY);
        lblRepliesHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        repliesPanel.add(lblRepliesHeader);
        repliesPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        if (replies.isEmpty()) {
            JLabel lblEmpty = new JLabel("Henüz yanıt bulunmuyor.");
            lblEmpty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblEmpty.setForeground(TEXT_SECONDARY);
            lblEmpty.setBorder(new EmptyBorder(10, 0, 10, 0));
            repliesPanel.add(lblEmpty);
        } else {
            for (TicketReply reply : replies) {
                JPanel replyCard = new JPanel();
                replyCard.setLayout(new BoxLayout(replyCard, BoxLayout.Y_AXIS));
                replyCard.setBackground(CARD_BG);
                replyCard.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        new EmptyBorder(10, 14, 10, 14)
                ));
                replyCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                replyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

                String userName = resolveUserName(reply.getUserId());
                String dateStr = reply.getCreatedAt() != null ? sdf.format(reply.getCreatedAt()) : "";

                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setBackground(CARD_BG);
                headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

                JLabel lblUser = new JLabel(userName + "  •  " + dateStr);
                lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lblUser.setForeground(TEXT_SECONDARY);
                headerPanel.add(lblUser, BorderLayout.WEST);

                boolean isAuthor = loggedInUser.getId() == reply.getUserId();
                boolean canEditOwnReply = isAuthor && !isTicketResolvedOrCancelled();
                if (isAdmin() || canEditOwnReply) {
                    JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
                    actionsPanel.setBackground(CARD_BG);

                    JButton btnEdit = new JButton("<html><u>Düzenle</u></html>");
                    btnEdit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    btnEdit.setForeground(PRIMARY);
                    btnEdit.setBorderPainted(false);
                    btnEdit.setContentAreaFilled(false);
                    btnEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    btnEdit.setMargin(new Insets(0, 0, 0, 0));
                    btnEdit.addActionListener(e -> editReplyAction(reply));
                    actionsPanel.add(btnEdit);

                    JButton btnDelete = new JButton("<html><u>Sil</u></html>");
                    btnDelete.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    btnDelete.setForeground(DANGER);
                    btnDelete.setBorderPainted(false);
                    btnDelete.setContentAreaFilled(false);
                    btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    btnDelete.setMargin(new Insets(0, 0, 0, 0));
                    btnDelete.addActionListener(e -> deleteReplyAction(reply));
                    actionsPanel.add(btnDelete);

                    headerPanel.add(actionsPanel, BorderLayout.EAST);
                }

                JLabel lblMsg = new JLabel("<html><body><div style='width: 300px;'>" + reply.getMessage() + "</div></body></html>");
                lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lblMsg.setForeground(TEXT_PRIMARY);
                lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);

                replyCard.add(headerPanel);
                replyCard.add(Box.createRigidArea(new Dimension(0, 8)));
                replyCard.add(lblMsg);

                repliesPanel.add(replyCard);
                repliesPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            }
        }

        repliesPanel.revalidate();
        repliesPanel.repaint();
    }

    // alt panel (yaz / atama)
    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        boolean isClosed = (ticket.getStatusId() == 3 || ticket.getStatusId() == 4);

        if (!isClosed) {
            txtReplyMessage = new JTextArea(3, 20);
            txtReplyMessage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            txtReplyMessage.setLineWrap(true);
            txtReplyMessage.setWrapStyleWord(true);
            txtReplyMessage.putClientProperty("JTextArea.placeholderText", "Yanıtınızı buraya yazınız...");
            JScrollPane replyScroll = new JScrollPane(txtReplyMessage);
            replyScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            replyScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
            panel.add(replyScroll);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            btnPanel.setBackground(CARD_BG);
            btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton btnSend = createStyledButton("Yanıt Gönder", PRIMARY);
            btnSend.addActionListener(e -> sendReply());
            btnPanel.add(btnSend);

            if (isAdmin() || isManagerOfDepartment()) {
                JButton btnAssign = createStyledButton("Çalışana Ata", WARNING);
                btnAssign.addActionListener(e -> assignTicketAction());
                btnPanel.add(btnAssign);

                JButton btnStatus = createStyledButton("Durumu Güncelle", SUCCESS);
                btnStatus.addActionListener(e -> updateStatusAction());
                btnPanel.add(btnStatus);
            }

            if (isAssignedEmployee()) {
                JButton btnStatus = createStyledButton("Durumu Güncelle", SUCCESS);
                btnStatus.addActionListener(e -> updateStatusAction());
                btnPanel.add(btnStatus);
            }

            if (isAdmin()) {
                JButton btnDeleteTicket = createStyledButton("Talebi Sil", DANGER);
                btnDeleteTicket.addActionListener(e -> deleteTicketAction());
                btnPanel.add(btnDeleteTicket);
            }

            panel.add(btnPanel);
        } else {
            JLabel lblClosed = new JLabel("Bu talep kapatılmıştır. Yanıt eklenemez.");
            lblClosed.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            lblClosed.setForeground(DANGER);
            lblClosed.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lblClosed);

            if (isAdmin()) {
                panel.add(Box.createRigidArea(new Dimension(0, 8)));
                JButton btnDeleteTicket = createStyledButton("Talebi Sil", DANGER);
                btnDeleteTicket.setAlignmentX(Component.LEFT_ALIGNMENT);
                btnDeleteTicket.addActionListener(e -> deleteTicketAction());
                panel.add(btnDeleteTicket);
            }
        }

        return panel;
    }

    // kim ne yapabiliyor
    private boolean isAdmin() {
        return loggedInUser.getRoleId() != null && loggedInUser.getRoleId() == 1;
    }

    // kapalı ticketta yorum kilidi
    private boolean isTicketResolvedOrCancelled() {
        int sid = ticket.getStatusId();
        return sid == 3 || sid == 4;
    }

    private boolean isManagerOfDepartment() {
        return loggedInUser.getRoleId() != null && loggedInUser.getRoleId() == 2
                && loggedInUser.getDepartmentId() != null
                && loggedInUser.getDepartmentId() == ticket.getTargetDepartmentId();
    }

    private boolean isAssignedEmployee() {
        return loggedInUser.getRoleId() != null && loggedInUser.getRoleId() == 3
                && ticket.getAssignedUserId() != null
                && ticket.getAssignedUserId() == loggedInUser.getId();
    }

    private void sendReply() {
        String message = txtReplyMessage.getText().trim();
        String error = ticketReplyService.addReply(ticket.getId(), loggedInUser.getId(), message);

        if (error == null) {
            txtReplyMessage.setText("");
            loadReplies();
        } else {
            JOptionPane.showMessageDialog(this, error, "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignTicketAction() {
        List<User> employees = userDAO.getUsersByDepartmentId(ticket.getTargetDepartmentId());

        List<User> onlyEmployees = new java.util.ArrayList<>();
        for (User u : employees) {
            if (u.getRoleId() != null && u.getRoleId() == 3 && u.isApproved()) {
                onlyEmployees.add(u);
            }
        }

        if (onlyEmployees.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bu departmanda atanabilecek çalışan bulunmuyor!", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer idx = showAssignEmployeeDialog(onlyEmployees);
        if (idx == null) return;

        String error = ticketService.assignTicket(ticket.getId(), onlyEmployees.get(idx).getId());
        if (error == null) {
            refreshTicketData();
        } else {
            JOptionPane.showMessageDialog(this, error, "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // placeholder iken OK kapalı
    private Integer showAssignEmployeeDialog(List<User> onlyEmployees) {
        final String placeholder = "Seçiniz...";
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Çalışana Ata", Dialog.ModalityType.APPLICATION_MODAL);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel msg = new JLabel("Atamak istediğiniz çalışanı seçiniz:");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.addItem(placeholder);
        for (User u : onlyEmployees) {
            combo.addItem(u.getFirstName() + " " + u.getLastName());
        }
        combo.setSelectedIndex(0);

        JButton btnOk = new JButton("OK");
        JButton btnCancel = new JButton("İptal");
        btnOk.setEnabled(false);
        combo.addActionListener(e -> btnOk.setEnabled(combo.getSelectedIndex() > 0));

        final int[] chosen = {-1};
        btnOk.addActionListener(e -> {
            chosen[0] = combo.getSelectedIndex() - 1;
            dialog.dispose();
        });
        btnCancel.addActionListener(e -> dialog.dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.add(btnCancel);
        buttons.add(btnOk);

        root.add(msg, BorderLayout.NORTH);
        root.add(combo, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        return chosen[0] >= 0 ? chosen[0] : null;
    }

    private void updateStatusAction() {
        List<TicketStatus> statuses = ticketStatusDAO.getAllStatuses();
        // Atanan çalışan: sadece 3=Çözüldü, 4=İptal Edildi (Ticket_Statuses); 1–2 seçilemez
        if (isAssignedEmployee()) {
            List<TicketStatus> allowed = new ArrayList<>();
            for (TicketStatus s : statuses) {
                int id = s.getId();
                if (id == 3 || id == 4) {
                    allowed.add(s);
                }
            }
            statuses = allowed;
        }

        if (statuses.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seçilebilir durum bulunmuyor.",
                    "Durum Güncelle", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] names = new String[statuses.size()];
        for (int i = 0; i < statuses.size(); i++) names[i] = statuses.get(i).getStatusName();

        String selected = (String) JOptionPane.showInputDialog(this, "Yeni durumu seçiniz:",
                "Durum Güncelle", JOptionPane.QUESTION_MESSAGE, null, names, names[0]);

        if (selected != null) {
            int statusId = -1;
            for (TicketStatus s : statuses) {
                if (s.getStatusName().equals(selected)) { statusId = s.getId(); break; }
            }

            if (statusId > 0) {
                String error = ticketService.updateTicketStatus(ticket.getId(), statusId);
                if (error == null) {
                    refreshTicketData();
                } else {
                    JOptionPane.showMessageDialog(this, error, "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void editReplyAction(TicketReply reply) {
        if (!isAdmin() && isTicketResolvedOrCancelled()) {
            JOptionPane.showMessageDialog(this,
                    "Çözülmüş veya iptal edilmiş taleplerde yorum düzenlenemez.",
                    "İşlem Yapılamaz", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextArea txtEdit = new JTextArea(reply.getMessage(), 3, 30);
        txtEdit.setLineWrap(true);
        txtEdit.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(txtEdit);

        int result = JOptionPane.showConfirmDialog(this, scroll, "Yorumu Düzenle", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String error = ticketReplyService.updateReply(reply.getId(), txtEdit.getText());
            if (error == null) {
                System.out.println("[BAŞARILI] Yanıt güncellendi.");
                loadReplies();
            } else {
                JOptionPane.showMessageDialog(this, error, "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteReplyAction(TicketReply reply) {
        if (!isAdmin() && isTicketResolvedOrCancelled()) {
            JOptionPane.showMessageDialog(this,
                    "Çözülmüş veya iptal edilmiş taleplerde yorum silinemez.",
                    "İşlem Yapılamaz", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bu yorumu kalıcı olarak silmek istediğinize emin misiniz?",
                "Yorumu Sil", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String error = ticketReplyService.deleteReply(reply.getId());
            if (error == null) {
                System.out.println("[BAŞARILI] Yanıt silindi.");
                loadReplies();
            } else {
                JOptionPane.showMessageDialog(this, error, "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteTicketAction() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "#" + ticket.getId() + " — " + ticket.getTitle() + "\nBu talep ve tüm yanıtları kalıcı olarak silinecek!\nBu işlem GERİ ALINAMAZ. Devam etmek istiyor musunuz?",
                "Talebi Sil", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            String error = ticketService.deleteTicket(ticket.getId());
            if (error == null) {
                dashboard.returnToListAfterTicketDeleted(new TicketListUI(loggedInUser, dashboard, TicketListUI.MODE_ALL));
            } else {
                JOptionPane.showMessageDialog(this, error, "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshTicketData() {
        this.ticket = ticketService.getTicketById(ticket.getId());
        dashboard.replaceContentPanel(new TicketDetailUI(ticket, loggedInUser, dashboard));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private Color getStatusColor(String statusName) {
        if ("Açık".equals(statusName)) return PRIMARY;
        if ("İşlemde".equals(statusName)) return WARNING;
        if ("Çözüldü".equals(statusName)) return SUCCESS;
        if ("İptal Edildi".equals(statusName)) return DANGER;
        return TEXT_SECONDARY;
    }

    private void addInfoCell(JPanel grid, String label, String value) {
        JPanel cell = new JPanel();
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        cell.setBackground(CARD_BG);

        JLabel lblKey = new JLabel(label);
        lblKey.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblKey.setForeground(TEXT_SECONDARY);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVal.setForeground(TEXT_PRIMARY);

        cell.add(lblKey);
        cell.add(lblVal);
        grid.add(cell);
    }

    private String resolveUserName(int userId) {
        User user = userDAO.getUserById(userId);
        return user != null ? user.getFirstName() + " " + user.getLastName() : "—";
    }

    private String resolveUserNameWithDept(int userId) {
        User user = userDAO.getUserById(userId);
        if (user == null) return "—";
        String name = user.getFirstName() + " " + user.getLastName();
        if (user.getDepartmentId() != null) {
            String dept = resolveDepartmentName(user.getDepartmentId());
            return name + " — " + dept;
        }
        return name;
    }

    private String resolveDepartmentName(int id) {
        Department dept = departmentDAO.getDepartmentById(id);
        return dept != null ? dept.getDepartmentName() : "—";
    }

    private String resolveCategoryName(int id) {
        Category cat = categoryDAO.getCategoryById(id);
        return cat != null ? cat.getCategoryName() : "—";
    }

    private String resolvePriorityName(int id) {
        Priority prio = priorityDAO.getPriorityById(id);
        return prio != null ? prio.getPriorityName() : "—";
    }

    private String resolveStatusName(int id) {
        TicketStatus status = ticketStatusDAO.getStatusById(id);
        return status != null ? status.getStatusName() : "—";
    }
}
