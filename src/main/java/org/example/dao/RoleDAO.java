package org.example.dao;

import org.example.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String query = "SELECT * FROM Roles ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Role role = mapRowToRole(rs);
                roles.add(role);
            }

        } catch (SQLException e) {
            System.err.println("Roller getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return roles;
    }

    public Role getRoleById(int id) {
        String query = "SELECT * FROM Roles WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowToRole(rs);
            }

        } catch (SQLException e) {
            System.err.println("Rol getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return null;
    }

    private Role mapRowToRole(ResultSet rs) throws SQLException {
        return new Role(
                rs.getInt("id"),
                rs.getString("role_name")
        );
    }
}
