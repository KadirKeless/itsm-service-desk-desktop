package org.example.ui;

import org.example.model.Category;
import org.example.model.Department;
import org.example.model.Priority;
import org.example.model.User;
import org.example.dao.CategoryDAO;
import org.example.dao.DepartmentDAO;
import org.example.dao.PriorityDAO;
import org.example.service.TicketService;
import org.example.utils.TextDocumentLimit;
import org.example.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

public class CreateTicketUI extends JPanel {

    private TicketService ticketService;
    private DepartmentDAO departmentDAO;
    private CategoryDAO categoryDAO;
    private PriorityDAO priorityDAO;
    private User loggedInUser;

    private JTextField txtTitle;
    private JTextArea txtDescription;
    private JComboBox<Department> cmbDepartment;
    private JComboBox<Category> cmbCategory;
    private JComboBox<Priority> cmbPriority;
    private JButton btnSubmit;

    public CreateTicketUI(User user, DashboardUI dashboard) {
        this.loggedInUser = user;
        this.ticketService = new TicketService();
        this.departmentDAO = new DepartmentDAO();
        this.categoryDAO = new CategoryDAO();
        this.priorityDAO = new PriorityDAO();

        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Başlık
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(241, 245, 249));

        JButton btnBack = new JButton(dashboard.getBackNavigationLabel());
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setForeground(new Color(37, 99, 235));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> dashboard.goBackOrHome());
        headerPanel.add(btnBack);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblHeader = new JLabel("Yeni Destek Talebi");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Sorununuzu detaylı bir şekilde açıklayınız");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        headerPanel.add(lblHeader);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        headerPanel.add(lblSub);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        add(headerPanel, BorderLayout.NORTH);

        // Kart (Form)
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(25, 30, 25, 30)
        ));

        Font inputFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Başlık alanı
        txtTitle = new JTextField();
        txtTitle.setFont(inputFont);
        txtTitle.putClientProperty("JTextField.placeholderText", "Sorununuzu özetleyen bir başlık yazınız");
        TextDocumentLimit.apply(txtTitle, ValidationUtils.TICKET_TITLE_MAX_LEN);
        addFormField(card, "Konu Başlığı (en fazla " + ValidationUtils.TICKET_TITLE_MAX_LEN + " karakter)", txtTitle);

        // Açıklama
        txtDescription = new JTextArea(4, 20);
        txtDescription.setFont(inputFont);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        TextDocumentLimit.apply(txtDescription, ValidationUtils.TICKET_DESCRIPTION_MAX_LEN);
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        scrollPane.setPreferredSize(new Dimension(400, 88));
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        addFormField(card, "Detaylı Açıklama (en fazla " + ValidationUtils.TICKET_DESCRIPTION_MAX_LEN + " karakter)", scrollPane);

        // Departman
        cmbDepartment = new JComboBox<>();
        cmbDepartment.setFont(inputFont);
        cmbDepartment.addItem(null);
        loadDepartments();
        cmbDepartment.setRenderer(createRenderer("Departman seçiniz..."));
        addFormField(card, "İlgili Departman", cmbDepartment);

        // Kategori
        cmbCategory = new JComboBox<>();
        cmbCategory.setFont(inputFont);
        cmbCategory.setEnabled(false);
        cmbCategory.setRenderer(createRenderer("Kategori seçiniz..."));
        addFormField(card, "Kategori", cmbCategory);

        // Öncelik
        cmbPriority = new JComboBox<>();
        cmbPriority.setFont(inputFont);
        cmbPriority.setEnabled(false);
        cmbPriority.addItem(null);
        loadPriorities();
        cmbPriority.setRenderer(createRenderer("Öncelik seçiniz..."));
        addFormField(card, "Öncelik Durumu", cmbPriority);

        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Gönder butonu
        btnSubmit = new JButton("Talebi Gönder");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setBackground(new Color(37, 99, 235));
        btnSubmit.setFocusPainted(false);
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSubmit.setMaximumSize(new Dimension(180, 42));
        btnSubmit.setEnabled(false);
        btnSubmit.addActionListener(e -> submitTicket());
        card.add(btnSubmit);

        add(card, BorderLayout.CENTER);

        setupDynamicFormLogic();
    }

    // dep seçilince kategori dolar
    private void setupDynamicFormLogic() {
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { checkFormReady(); }
            public void removeUpdate(DocumentEvent e) { checkFormReady(); }
            public void changedUpdate(DocumentEvent e) { checkFormReady(); }
        };
        txtTitle.getDocument().addDocumentListener(docListener);
        txtDescription.getDocument().addDocumentListener(docListener);

        cmbDepartment.addActionListener(e -> {
            Department selectedDept = (Department) cmbDepartment.getSelectedItem();
            cmbCategory.removeAllItems();
            cmbCategory.addItem(null);

            if (selectedDept != null) {
                List<Category> categories = categoryDAO.getCategoriesByDepartmentId(selectedDept.getId());
                for (Category cat : categories) {
                    cmbCategory.addItem(cat);
                }
                cmbCategory.setEnabled(true);
            } else {
                cmbCategory.setEnabled(false);
                cmbPriority.setEnabled(false);
            }
            checkFormReady();
        });

        cmbCategory.addActionListener(e -> {
            Category selectedCat = (Category) cmbCategory.getSelectedItem();
            if (selectedCat != null) {
                cmbPriority.setEnabled(true);
            } else {
                cmbPriority.setSelectedIndex(0);
                cmbPriority.setEnabled(false);
            }
            checkFormReady();
        });

        cmbPriority.addActionListener(e -> checkFormReady());
    }

    private void checkFormReady() {
        boolean ready = !txtTitle.getText().trim().isEmpty()
                && !txtDescription.getText().trim().isEmpty()
                && cmbDepartment.getSelectedItem() != null
                && cmbCategory.getSelectedItem() != null
                && cmbPriority.getSelectedItem() != null;
        btnSubmit.setEnabled(ready);
    }

    private void loadDepartments() {
        for (Department dept : departmentDAO.getAllDepartments()) {
            // kendi dep'ı listede yok
            if (loggedInUser.getDepartmentId() != null && loggedInUser.getDepartmentId() == dept.getId()) {
                continue;
            }
            cmbDepartment.addItem(dept);
        }
    }

    private void loadPriorities() {
        for (Priority prio : priorityDAO.getAllPriorities()) cmbPriority.addItem(prio);
    }

    private void submitTicket() {
        String title = txtTitle.getText().trim();
        String desc = txtDescription.getText().trim();
        Department dept = (Department) cmbDepartment.getSelectedItem();
        Category cat = (Category) cmbCategory.getSelectedItem();
        Priority prio = (Priority) cmbPriority.getSelectedItem();

        String error = ticketService.createTicket(title, desc, loggedInUser.getId(), dept.getId(), cat.getId(), prio.getId());

        if (error == null) {
            System.out.println("[BAŞARILI] Talep oluşturuldu.");
            txtTitle.setText("");
            txtDescription.setText("");
            cmbDepartment.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, error, "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(51, 65, 85));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (!(field instanceof JScrollPane)) {
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        }

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
    }

    // boş seçimde placeholder
    private DefaultListCellRenderer createRenderer(String placeholder) {
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