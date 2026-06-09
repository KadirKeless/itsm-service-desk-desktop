package org.example.dao;

import org.example.model.TicketReply;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TicketReplyDAO {

    public boolean addReply(TicketReply reply) {
        String query = "INSERT INTO Ticket_Replies (ticket_id, user_id, message) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reply.getTicketId());
            pstmt.setInt(2, reply.getUserId());
            pstmt.setString(3, reply.getMessage());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Yanıt eklenirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    public List<TicketReply> getRepliesByTicketId(int ticketId) {
        List<TicketReply> replies = new ArrayList<>();
        String query = "SELECT * FROM Ticket_Replies WHERE ticket_id = ? ORDER BY created_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, ticketId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                replies.add(mapRowToTicketReply(rs));
            }

        } catch (SQLException e) {
            System.err.println("Yanıtlar getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return replies;
    }

    public boolean updateReply(int replyId, String newMessage) {
        String query = "UPDATE Ticket_Replies SET message = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newMessage);
            pstmt.setInt(2, replyId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Yanıt güncellenirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteReply(int replyId) {
        String query = "DELETE FROM Ticket_Replies WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, replyId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Yanıt silinirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    private TicketReply mapRowToTicketReply(ResultSet rs) throws SQLException {
        return new TicketReply(
                rs.getInt("id"),
                rs.getInt("ticket_id"),
                rs.getInt("user_id"),
                rs.getString("message"),
                rs.getTimestamp("created_at")
        );
    }
}
