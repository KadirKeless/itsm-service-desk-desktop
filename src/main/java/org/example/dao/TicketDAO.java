package org.example.dao;

import org.example.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public boolean createTicket(Ticket ticket) {
        String query = "INSERT INTO Tickets (title, description, requester_user_id, target_department_id, category_id, priority_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, ticket.getTitle());
            pstmt.setString(2, ticket.getDescription());
            pstmt.setInt(3, ticket.getRequesterUserId());
            pstmt.setInt(4, ticket.getTargetDepartmentId());
            pstmt.setInt(5, ticket.getCategoryId());
            pstmt.setInt(6, ticket.getPriorityId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Talep oluşturulurken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    public Ticket getTicketById(int id) {
        String query = "SELECT * FROM Tickets WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowToTicket(rs);
            }

        } catch (SQLException e) {
            System.err.println("Talep getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return null;
    }

    public List<Ticket> getTicketsByRequesterId(int requesterId) {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM Tickets WHERE requester_user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, requesterId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tickets.add(mapRowToTicket(rs));
            }

        } catch (SQLException e) {
            System.err.println("Kullanıcı talepleri getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return tickets;
    }

    public List<Ticket> getTicketsByDepartmentId(int departmentId) {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM Tickets WHERE target_department_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, departmentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tickets.add(mapRowToTicket(rs));
            }

        } catch (SQLException e) {
            System.err.println("Departman talepleri getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return tickets;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM Tickets ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tickets.add(mapRowToTicket(rs));
            }

        } catch (SQLException e) {
            System.err.println("Tüm talepler getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return tickets;
    }

    public List<Ticket> getTicketsByAssignedUserId(int assignedUserId) {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM Tickets WHERE assigned_user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, assignedUserId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tickets.add(mapRowToTicket(rs));
            }

        } catch (SQLException e) {
            System.err.println("Atanan talepler getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return tickets;
    }

    public boolean updateTicketStatus(int ticketId, int statusId) {
        String query = "UPDATE Tickets SET status_id = ? WHERE id = ?";
        // 2 → işleme alındı say, 3/4 → kapanış zamanı da yaz
        if (statusId == 2) {
            query = "UPDATE Tickets SET status_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        } else if (statusId == 3 || statusId == 4) {
            query = "UPDATE Tickets SET status_id = ?, closed_at = CURRENT_TIMESTAMP WHERE id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, statusId);
            pstmt.setInt(2, ticketId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Talep durumu güncellenirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    public boolean assignTicket(int ticketId, int assignedUserId) {
        String query = "UPDATE Tickets SET assigned_user_id = ?, status_id = 2, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, assignedUserId);
            pstmt.setInt(2, ticketId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Talep atanırken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    public boolean closeTicket(int ticketId, int statusId) {
        String query = "UPDATE Tickets SET status_id = ?, closed_at = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, statusId);
            pstmt.setInt(2, ticketId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Talep kapatılırken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    // cascade ile yanıtlar da gider
    public boolean deleteTicket(int ticketId) {
        String query = "DELETE FROM Tickets WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, ticketId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Talep silinirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    // dondurmadan önce: hâlâ açık/işlemde atanan ticket var mı
    public int countAssignedOpenOrInProgressTickets(int userId) {
        String query = "SELECT COUNT(*) FROM Tickets WHERE assigned_user_id = ? AND status_id IN (1, 2)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Atanan aktif talep sayısı alınırken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return 0;
    }

    private Ticket mapRowToTicket(ResultSet rs) throws SQLException {
        Integer assignedUserId = (Integer) rs.getObject("assigned_user_id");

        Timestamp closedAt = rs.getTimestamp("closed_at");

        return new Ticket(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("requester_user_id"),
                rs.getInt("target_department_id"),
                rs.getInt("category_id"),
                rs.getInt("priority_id"),
                rs.getInt("status_id"),
                assignedUserId,
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at"),
                closedAt
        );
    }
}