package org.example.dao;

import org.example.model.TicketStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketStatusDAO {

    public List<TicketStatus> getAllStatuses() {
        List<TicketStatus> statuses = new ArrayList<>();
        String query = "SELECT * FROM Ticket_Statuses ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                TicketStatus status = mapRowToTicketStatus(rs);
                statuses.add(status);
            }

        } catch (SQLException e) {
            System.err.println("Durumlar getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return statuses;
    }

    public TicketStatus getStatusById(int id) {
        String query = "SELECT * FROM Ticket_Statuses WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowToTicketStatus(rs);
            }

        } catch (SQLException e) {
            System.err.println("Durum getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return null;
    }

    private TicketStatus mapRowToTicketStatus(ResultSet rs) throws SQLException {
        return new TicketStatus(
                rs.getInt("id"),
                rs.getString("status_name")
        );
    }
}
