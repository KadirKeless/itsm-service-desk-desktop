package org.example.dao;

import org.example.model.Priority;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PriorityDAO {

    public List<Priority> getAllPriorities() {
        List<Priority> priorities = new ArrayList<>();
        String query = "SELECT * FROM Priorities ORDER BY level_weight ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Priority priority = mapRowToPriority(rs);
                priorities.add(priority);
            }

        } catch (SQLException e) {
            System.err.println("Öncelikler getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return priorities;
    }

    // ID'ye göre tek bir öncelik getirir.
    public Priority getPriorityById(int id) {
        String query = "SELECT * FROM Priorities WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowToPriority(rs);
            }

        } catch (SQLException e) {
            System.err.println("Öncelik getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return null;
    }

    private Priority mapRowToPriority(ResultSet rs) throws SQLException {
        return new Priority(
                rs.getInt("id"),
                rs.getString("priority_name"),
                rs.getInt("level_weight")
        );
    }
}
