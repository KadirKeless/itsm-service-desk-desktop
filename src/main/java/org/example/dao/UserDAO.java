package org.example.dao;

import org.example.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean registerUser(User user) {
        // dep/rol null, onay 0 — tablo default ile uyumlu
        String query = "INSERT INTO Users (first_name, last_name, email, password, is_approved) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setBoolean(5, user.isApproved());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Veritabanına kullanıcı eklenirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    public boolean createApprovedUser(String firstName, String lastName, String email, String password, Integer roleId, Integer departmentId) {
        String query = "INSERT INTO Users (first_name, last_name, email, password, is_approved, role_id, department_id) VALUES (?, ?, ?, ?, 1, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setObject(5, roleId, java.sql.Types.INTEGER);
            pstmt.setObject(6, departmentId, java.sql.Types.INTEGER);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Admin tarafından kullanıcı eklenirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    public User loginUser(String email, String password) {
        String query = "SELECT * FROM Users WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }

        } catch (SQLException e) {
            System.err.println("Giriş işlemi sırasında SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return null;
    }

    public User getUserById(int id) {
        String query = "SELECT * FROM Users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapRowToUser(rs);
            }

        } catch (SQLException e) {
            System.err.println("Kullanıcı getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return null;
    }

    public User getUserByEmail(String email) {
        String query = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapRowToUser(rs);
        } catch (SQLException e) {
            System.err.println("E-posta ile kullanıcı getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Kullanıcılar listelenirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return users;
    }

    public List<User> getUnapprovedUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE is_approved = 0 AND role_id IS NULL ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Onay bekleyen kullanıcılar getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return users;
    }

    public List<User> getUsersByDepartmentId(int departmentId) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users WHERE department_id = ? AND is_approved = 1 ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, departmentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Departman kullanıcıları getirilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return users;
    }

    public boolean approveUser(int userId, int roleId, int departmentId) {
        String query = "UPDATE Users SET is_approved = 1, role_id = ?, department_id = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, roleId);
            pstmt.setInt(2, departmentId);
            pstmt.setInt(3, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Kullanıcı onaylanırken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    // dep başına max 1 yönetici — kotayı buradan sayıyoruz
    public int getManagerCountByDepartmentId(int departmentId) {
        String query = "SELECT COUNT(*) FROM Users WHERE department_id = ? AND role_id = 2 AND is_approved = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, departmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Yönetici sayısı kontrol edilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return 0;
    }

    // çalışan kotası (max 5)
    public int getEmployeeCountByDepartmentId(int departmentId) {
        String query = "SELECT COUNT(*) FROM Users WHERE department_id = ? AND role_id = 3 AND is_approved = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, departmentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Çalışan sayısı kontrol edilirken SQL Hatası: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
        }

        return 0;
    }

    public boolean freezeUser(int userId) {
        String query = "UPDATE Users SET is_approved = 0 WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Kullanıcı dondurulurken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    public boolean activateUser(int userId) {
        String query = "UPDATE Users SET is_approved = 1 WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Kullanıcı aktifleştirilirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    // FK yüzünden bağlı ticket varsa patlayabilir
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM Users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Kullanıcı silinirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser(int userId, String firstName, String lastName, String email, Integer roleId, Integer departmentId) {
        String query = "UPDATE Users SET first_name = ?, last_name = ?, email = ?, role_id = ?, department_id = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setObject(4, roleId, java.sql.Types.INTEGER);
            pstmt.setObject(5, departmentId, java.sql.Types.INTEGER);
            pstmt.setInt(6, userId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Kullanıcı güncellenirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Bağlantı Hatası: " + e.getMessage());
            return false;
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        Integer departmentId = (Integer) rs.getObject("department_id");
        Integer roleId = (Integer) rs.getObject("role_id");
        String profilePicturePath = rs.getString("profile_picture");

        return new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("password"),
                departmentId,
                roleId,
                rs.getBoolean("is_approved"),
                profilePicturePath
        );
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        String query = "UPDATE Users SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Şifre güncellenirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateProfilePicture(int userId, String imagePath) {
        String sql = "UPDATE Users SET profile_picture = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            if (imagePath != null) {
                ps.setString(1, imagePath);
            } else {
                ps.setNull(1, java.sql.Types.VARCHAR);
            }
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Profil resmi güncellenirken SQL Hatası: " + e.getMessage());
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}