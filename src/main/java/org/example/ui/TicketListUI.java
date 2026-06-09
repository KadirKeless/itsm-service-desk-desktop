package org.example.ui;

import org.example.model.Department;
import org.example.model.Priority;
import org.example.model.Ticket;
import org.example.model.TicketStatus;
import org.example.model.User;
import org.example.dao.DepartmentDAO;
import org.example.dao.PriorityDAO;
import org.example.dao.TicketStatusDAO;
import org.example.dao.UserDAO;
import org.example.service.TicketService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketListUI extends JPanel {

    public static final int MODE_MY_TICKETS = 1;
    public static final int MODE_DEPARTMENT = 2;
    public static final int MODE_ALL = 3;
    public static final int MODE_ASSIGNED = 4;

    private TicketService ticketService;
    private DepartmentDAO departmentDAO;
    private PriorityDAO priorityDAO;
    private TicketStatusDAO ticketStatusDAO;
    private UserDAO userDAO;
    private User loggedInUser;
    private DashboardUI dashboard;
    private int mode;

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter; // Sıralama motoru
    private List<Ticket> currentTicketList;

    // Öncelik adı → ID eşleme (veritabanındaki sıralamayı korumak için)
    private Map<String, Integer> priorityIdMap;

    public TicketListUI(User user, DashboardUI dashboard, int mode) {
        this.loggedInUser = user;
        this.dashboard = dashboard;
        this.mode = mode;
        this.ticketService = new TicketService();
        this.departmentDAO = new DepartmentDAO();
        this.priorityDAO = new PriorityDAO();
        this.ticketStatusDAO = new TicketStatusDAO();
        this.userDAO = new UserDAO();
        this.priorityIdMap = new HashMap<>();

        // Öncelik ID haritasını yükle
        buildPriorityIdMap();

        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // başlık
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(241, 245, 249));

        JPanel westColumn = new JPanel();
        westColumn.setLayout(new BoxLayout(westColumn, BoxLayout.Y_AXIS));
        westColumn.setBackground(new Color(241, 245, 249));

        JButton btnBack = new JButton(dashboard.getBackNavigationLabel());
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setForeground(new Color(37, 99, 235));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> dashboard.goBackOrHome());
        westColumn.add(btnBack);
        westColumn.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(new Color(241, 245, 249));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHeader = new JLabel(getTitleByMode());
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblHeader.setForeground(new Color(30, 41, 59));

        JLabel lblSub = new JLabel("Sütun başlıklarına tıklayarak sıralayabilirsiniz • Detay için çift tıklayınız");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));

        titlePanel.add(lblHeader);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 3)));
        titlePanel.add(lblSub);

        westColumn.add(titlePanel);

        JButton btnRefresh = new JButton("Yenile");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadTickets());

        headerPanel.add(westColumn, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // tablo
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // 0=ID, 1=Başlık, 2=Talep Eden, 3=Atanan, 4=Departman, 5=Öncelik, 6=Durum, 7=Tarih
        String[] columns = new String[]{"ID", "Başlık", "Talep Eden", "Atanan", "Departman", "Öncelik", "Durum", "Tarih"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }

            // Tüm sütunlar Object (sola hizalı) — ID dahil
            @Override
            public Class<?> getColumnClass(int column) {
                return Object.class;
            }
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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // sıralama
        rowSorter = new TableRowSorter<>(tableModel);

        // Sütun 0 (ID): Sayısal sıralama
        rowSorter.setComparator(0, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                int id1 = (o1 instanceof Integer) ? (Integer) o1 : Integer.parseInt(o1.toString());
                int id2 = (o2 instanceof Integer) ? (Integer) o2 : Integer.parseInt(o2.toString());
                return Integer.compare(id1, id2);
            }
        });

        int prioCol   = 5;
        int statusCol = 6;

        // Öncelik sütunu: Veritabanı ID'sine göre sırala (alfabetik değil!)
        rowSorter.setComparator(prioCol, new Comparator<String>() {
            @Override
            public int compare(String prio1, String prio2) {
                int id1 = priorityIdMap.getOrDefault(prio1, 0);
                int id2 = priorityIdMap.getOrDefault(prio2, 0);
                return Integer.compare(id1, id2);
            }
        });

        // Diğer sütunlar varsayılan (alphabetical / natural order) sıralama kullanır
        table.setRowSorter(rowSorter);

        // Durum sütunu renklendirme
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }

                // Durum sütunu renklendirme
                if (col == statusCol && value != null) {
                    String status = value.toString();
                    if (status.equals("Açık")) setForeground(new Color(37, 99, 235));
                    else if (status.equals("İşlemde")) setForeground(new Color(217, 119, 6));
                    else if (status.equals("Çözüldü")) setForeground(new Color(22, 163, 74));
                    else if (status.equals("İptal Edildi")) setForeground(new Color(220, 53, 69));
                    else setForeground(new Color(30, 41, 59));
                } else if (!isSelected) {
                    setForeground(new Color(30, 41, 59));
                }

                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        // Çift tıkla → Talep Detayı (sıralanmış tabloda doğru satırı bulmak için convertRowIndexToModel)
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) openTicketDetail();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        loadTickets();
    }

    // öncelik adı → id (sıralama için)
    // Veritabanındaki Priorities tablosundan isimlerin ID karşılıklarını çeker.
    // Sıralama sırasında "Düşük" < "Orta" < "Yüksek" < "Acil" şeklinde çalışır.
    private void buildPriorityIdMap() {
        var priorities = priorityDAO.getAllPriorities();
        for (var prio : priorities) {
            priorityIdMap.put(prio.getPriorityName(), prio.getId());
        }
    }

    private String getTitleByMode() {
        switch (mode) {
            case MODE_MY_TICKETS: return "Taleplerim";
            case MODE_DEPARTMENT: return "Departman Talepleri";
            case MODE_ALL: return "Tüm Talepler";
            case MODE_ASSIGNED: return "Bana Atanan Talepler";
            default: return "Talepler";
        }
    }

    private void loadTickets() {
        tableModel.setRowCount(0);

        switch (mode) {
            case MODE_MY_TICKETS: currentTicketList = ticketService.getTicketsByRequesterId(loggedInUser.getId()); break;
            case MODE_DEPARTMENT: currentTicketList = ticketService.getTicketsByDepartmentId(loggedInUser.getDepartmentId()); break;
            case MODE_ALL: currentTicketList = ticketService.getAllTickets(); break;
            case MODE_ASSIGNED: currentTicketList = ticketService.getTicketsByAssignedUserId(loggedInUser.getId()); break;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        for (Ticket ticket : currentTicketList) {
            String deptName    = resolveDepartmentName(ticket.getTargetDepartmentId());
            String prioName    = resolvePriorityName(ticket.getPriorityId());
            String statusName  = resolveStatusName(ticket.getStatusId());
            String dateStr     = ticket.getCreatedAt() != null ? sdf.format(ticket.getCreatedAt()) : "-";

            User requester = userDAO.getUserById(ticket.getRequesterUserId());
            String requesterName = requester != null
                    ? requester.getFirstName() + " " + requester.getLastName() : "—";

            String assignedName = "—";
            if (ticket.getAssignedUserId() != null) {
                User assigned = userDAO.getUserById(ticket.getAssignedUserId());
                if (assigned != null) assignedName = assigned.getFirstName() + " " + assigned.getLastName();
            }

            tableModel.addRow(new Object[]{ticket.getId(), ticket.getTitle(),
                    requesterName, assignedName, deptName, prioName, statusName, dateStr});
        }
    }

    // ÖNEMLİ: table.getSelectedRow() sıralanmış view index'ini döner.
    // convertRowIndexToModel() ile gerçek model index'ine çeviriyoruz.
    private void openTicketDetail() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;

        int modelRow = table.convertRowIndexToModel(viewRow);
        Ticket selectedTicket = currentTicketList.get(modelRow);
        dashboard.showPanel(new TicketDetailUI(selectedTicket, loggedInUser, dashboard));
    }

    private String resolveDepartmentName(int id) {
        Department dept = departmentDAO.getDepartmentById(id);
        return dept != null ? dept.getDepartmentName() : "—";
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
