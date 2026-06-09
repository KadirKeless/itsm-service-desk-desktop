package org.example.dao;

import org.example.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getCategoriesByDepartmentId(int departmentId) {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM Categories WHERE department_id = ? ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, departmentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Category cat = mapRowToCategory(rs);
                categories.add(cat);
            }

        } catch (SQLException e) {
            System.err.println("Kategoriler getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return categories;
    }

    public Category getCategoryById(int id) {
        String query = "SELECT * FROM Categories WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowToCategory(rs);
            }

        } catch (SQLException e) {
            System.err.println("Kategori getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return null;
    }

    private Category mapRowToCategory(ResultSet rs) throws SQLException {
        return new Category(
                rs.getInt("id"),
                rs.getString("category_name"),
                rs.getInt("department_id")
        );
    }
}
