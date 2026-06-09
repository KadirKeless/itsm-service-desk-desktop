package org.example.service;

import org.example.dao.TicketDAO;
import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.utils.ValidationUtils;

import java.util.List;

public class UserService {

    // DAO'lar sadece burada; UI doğrudan DB'ye gitmesin diye
    private UserDAO userDAO;
    private TicketDAO ticketDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.ticketDAO = new TicketDAO();
    }

    // Kayıt — önce validasyon, sonra DAO
    public boolean register(String firstName, String lastName, String email, String password) {

        // ad/soyad boş olmasın
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            System.out.println("[HATA] Ad ve soyad alanları boş bırakılamaz!");
            return false;
        }

        // e-posta formatı
        String emailErr = ValidationUtils.validateEmail(email);
        if (emailErr != null) {
            System.out.println("[HATA] " + emailErr);
            return false;
        }

        // şifre kuralları (ValidationUtils)
        String passErr = ValidationUtils.validatePassword(password);
        if (passErr != null) {
            System.out.println("[HATA] " + passErr);
            return false;
        }

        // yeni kullanıcı: dep/rol null, onay kapalı
        User newUser = new User(firstName, lastName, email, password);

        // kaydet
        boolean isSaved = userDAO.registerUser(newUser);

        if (isSaved) {
            System.out.println("[BAŞARILI] Kayıt tamamlandı! Admin onayı bekleniyor.");
        } else {
            System.out.println("[HATA] Kayıt başarısız! (E-posta zaten kullanımda olabilir).");
        }

        return isSaved;
    }

    // Giriş
    public User login(String email, String password) {

        // boş alan bırakma
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("[HATA] E-posta ve şifre boş bırakılamaz!");
            return null;
        }

        User user = userDAO.loginUser(email, password);

        if (user == null) {
            System.out.println("[HATA] E-posta veya şifre hatalı!");
            return null;
        }

        // onaysızsa yine user dön (ekranda uyarı için)
        if (!user.isApproved()) {
            System.out.println("[UYARI] Hesap henüz Admin tarafından onaylanmamış.");
            return user;
        }

        System.out.println("[BAŞARILI] Hoş geldin, " + user.getFirstName());
        return user;
    }

    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }

    public User getUserByEmail(String email) {
        return userDAO.getUserByEmail(email);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public List<User> getUnapprovedUsers() {
        return userDAO.getUnapprovedUsers();
    }

    public List<User> getUsersByDepartmentId(int departmentId) {
        return userDAO.getUsersByDepartmentId(departmentId);
    }

    // Aktifleştir: ya ilk kez onay (rol/dep boş) ya da dondurulmuşu aç — ikisi farklı dallar
    public String activateUser(int userId, Integer roleId, Integer departmentId) {

        User target = userDAO.getUserById(userId);
        if (target == null) return "Kullanıcı sistemde bulunamadı!";

        // zaten açıksa iş yok
        if (target.isApproved()) {
            return "Bu kullanıcı zaten aktif durumda!";
        }

        boolean needsAssignment = (target.getRoleId() == null || target.getDepartmentId() == null);

        if (needsAssignment) {
            // ilk onay: rol + dep şart
            if (roleId == null || departmentId == null) {
                return "Bu kullanıcıya önce bir rol ve departman atamalısınız!";
            }

            if (roleId == 1) { // admin verme
                return "Admin rolü yeni kullanıcılara atanamaz!";
            }

            if (roleId == 2) { // yönetici kotası 1
                int managerCount = userDAO.getManagerCountByDepartmentId(departmentId);
                if (managerCount >= 1) {
                    return "Bu departmanda zaten bir Yönetici bulunuyor! (Maksimum: 1)";
                }
            }

            if (roleId == 3) { // çalışan kotası 5
                int employeeCount = userDAO.getEmployeeCountByDepartmentId(departmentId);
                if (employeeCount >= 5) {
                    return "Bu departmanda çalışan kapasitesi dolu! (Maksimum: 5)";
                }
            }

            boolean isSuccess = userDAO.approveUser(userId, roleId, departmentId);
            if (isSuccess) {
                System.out.println("[BAŞARILI] Kullanıcı onaylandı ve atandı (ID: " + userId + ")");
                return null;
            }
            return "İşlem sırasında bir hata oluştu. Lütfen tekrar deneyiniz.";

        } else {
            // eski rol/dep ile geri aç — yine kotaya bak
            Integer userRoleId = target.getRoleId();
            Integer userDeptId = target.getDepartmentId();

            if (userRoleId != null && userDeptId != null) {
                if (userRoleId == 2) {
                    int managerCount = userDAO.getManagerCountByDepartmentId(userDeptId);
                    if (managerCount >= 1) {
                        return "Bu departmanda zaten aktif bir Yönetici var! Kullanıcı aktifleştirilemez. (Maksimum: 1)";
                    }
                }
                if (userRoleId == 3) {
                    int employeeCount = userDAO.getEmployeeCountByDepartmentId(userDeptId);
                    if (employeeCount >= 5) {
                        return "Bu departmanın çalışan kapasitesi dolu! Kullanıcı aktifleştirilemez. (Maksimum: 5)";
                    }
                }
            }

            boolean isSuccess = userDAO.activateUser(userId);
            if (isSuccess) {
                System.out.println("[BAŞARILI] Kullanıcı aktifleştirildi (ID: " + userId + ")");
                return null;
            }
            return "İşlem sırasında bir hata oluştu. Lütfen tekrar deneyiniz.";
        }
    }

    // Dondur (is_approved 0)
    public String freezeUser(int userId) {

        User target = userDAO.getUserById(userId);
        if (target == null) return "Kullanıcı sistemde bulunamadı!";

        // zaten kapalıysa
        if (!target.isApproved()) {
            return "Bu kullanıcı zaten pasif durumda!";
        }

        if (target.getRoleId() != null && target.getRoleId() == 1) { // admin'e dokunma
            return "Admin hesabı dondurulamaz!";
        }

        int activeAssigned = ticketDAO.countAssignedOpenOrInProgressTickets(userId);
        if (activeAssigned > 0) {
            return "Bu kullanıcıya atanmış ve hâlen Açık veya İşlemde olan " + activeAssigned
                    + " talep bulunmaktadır. Dondurma yapılamaz; önce bu talepleri Çözüldü veya İptal Edildi durumuna getiriniz.";
        }

        boolean isSuccess = userDAO.freezeUser(userId);
        if (isSuccess) {
            System.out.println("[BAŞARILI] Kullanıcı donduruldu (ID: " + userId + ")");
            return null;
        }
        return "İşlem sırasında bir hata oluştu. Lütfen tekrar deneyiniz.";
    }

    public String deleteUser(int userId) {

        User target = userDAO.getUserById(userId);
        if (target == null) return "Kullanıcı sistemde bulunamadı!";

        // admin silinmez
        if (target.getRoleId() != null && target.getRoleId() == 1) {
            return "Admin hesabı silinemez!";
        }

        boolean isSuccess = userDAO.deleteUser(userId);
        if (isSuccess) {
            System.out.println("[BAŞARILI] Kullanıcı silindi (ID: " + userId + ")");
            return null;
        }
        return "Kullanıcı silinemedi! Bu kullanıcıya ait talepler bulunuyor olabilir.";
    }

    public String updateUser(int userId, String firstName, String lastName, String email, Integer roleId, Integer departmentId) {

        User target = userDAO.getUserById(userId);
        if (target == null) return "Kullanıcı sistemde bulunamadı!";

        // admin satırını düzenleme
        if (target.getRoleId() != null && target.getRoleId() == 1) {
            return "Admin hesabı üzerinde değişiklik yapılamaz!";
        }

        // isim boş olmasın
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            return "Ad ve soyad alanları boş bırakılamaz!";
        }

        // rol + dep seçili olmalı
        if (roleId == null) {
            return "Lütfen geçerli bir rol seçiniz!";
        }
        if (departmentId == null) {
            return "Lütfen geçerli bir departman seçiniz!";
        }

        // basit e-posta kontrolü (UI'da da var)
        if (email == null || !email.contains("@") || !email.contains(".")) {
            return "Geçerli bir e-posta adresi giriniz!";
        }

        // admin rolü yok
        if (roleId != null && roleId == 1) {
            return "Admin rolü atanamaz!";
        }

        // rol/dep değiştiyse kotaya tekrar bak
        if (roleId != null && departmentId != null) {
            boolean roleChanged = !roleId.equals(target.getRoleId());
            boolean deptChanged = !departmentId.equals(target.getDepartmentId());

            if (roleChanged || deptChanged) {
                if (roleId == 2) {
                    int managerCount = userDAO.getManagerCountByDepartmentId(departmentId);
                    if (managerCount >= 1) {
                        return "Bu departmanda zaten bir Yönetici bulunuyor! (Maksimum: 1)";
                    }
                }
                if (roleId == 3) {
                    int employeeCount = userDAO.getEmployeeCountByDepartmentId(departmentId);
                    if (employeeCount >= 5) {
                        return "Bu departmanda çalışan kapasitesi dolu! (Maksimum: 5)";
                    }
                }
            }
        }

        boolean isSuccess = userDAO.updateUser(userId, firstName, lastName, email, roleId, departmentId);
        if (isSuccess) {
            System.out.println("[BAŞARILI] Kullanıcı bilgileri güncellendi (ID: " + userId + ")");
            return null;
        }
        return "İşlem sırasında bir hata oluştu. Lütfen tekrar deneyiniz.";
    }

    // Admin direkt kullanıcı ekler (zaten onaylı kayıt)
    public String adminCreateUser(String firstName, String lastName, String email, String password, Integer roleId, Integer departmentId) {

        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            return "Ad ve soyad alanları boş bırakılamaz!";
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            return "Geçerli bir e-posta adresi giriniz!";
        }
        if (password == null || password.length() < 6) {
            return "Şifre en az 6 karakter olmalıdır!";
        }

        if (roleId == null) {
            return "Lütfen atama için bir rol seçiniz!";
        }
        if (departmentId == null) {
            return "Lütfen atama için departman seçiniz!";
        }

        if (roleId != null && roleId == 1) { // admin rolü yeni kullanıcıya yok
            return "Admin rolü yeni kullanıcılara atanamaz!";
        }

        if (roleId != null && departmentId != null) {
            if (roleId == 2) {
                int managerCount = userDAO.getManagerCountByDepartmentId(departmentId);
                if (managerCount >= 1) {
                    return "Bu departmanda zaten bir Yönetici bulunuyor! (Maksimum: 1)";
                }
            }
            if (roleId == 3) {
                int employeeCount = userDAO.getEmployeeCountByDepartmentId(departmentId);
                if (employeeCount >= 5) {
                    return "Bu departmanda çalışan kapasitesi dolu! (Maksimum: 5)";
                }
            }
        }

        boolean isSuccess = userDAO.createApprovedUser(firstName, lastName, email, password, roleId, departmentId);
        if (isSuccess) {
            System.out.println("[BAŞARILI] Admin tarafından yeni kullanıcı eklendi.");
            return null;
        }
        return "İşlem başarısız! E-posta adresi kullanımda olabilir.";
    }

    public String changePassword(int userId, String currentPassword, String newPassword) {
        User target = userDAO.getUserById(userId);
        if (target == null) return "Kullanıcı bulunamadı!";

        if (!target.getPassword().equals(currentPassword)) {
            return "Mevcut şifrenizi yanlış girdiniz!";
        }

        String passErr = ValidationUtils.validatePassword(newPassword);
        if (passErr != null) return passErr;

        if (currentPassword.equals(newPassword)) {
            return "Yeni şifre, mevcut şifre ile aynı olamaz!";
        }

        boolean success = userDAO.updateUserPassword(userId, newPassword);
        if (success) {
            target.setPassword(newPassword); // objeyi de güncelle
            return null;
        }
        return "Şifre güncellenirken veritabanında bir hata oluştu!";
    }

    public String updateProfilePicture(int userId, byte[] imageBytes) {
        if (imageBytes != null && imageBytes.length > 5 * 1024 * 1024) {
            return "Profil resmi boyutu 5 MB'ı geçemez! Lütfen daha küçük bir resim seçin.";
        }
        
        String imagePath = null;
        if (imageBytes != null && imageBytes.length > 0) {
            imagePath = "assets-avatars/avatar_" + userId + ".png";
            try {
                java.io.File dir = new java.io.File("assets-avatars");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                java.nio.file.Files.write(java.nio.file.Paths.get(imagePath), imageBytes);
            } catch (java.io.IOException e) {
                return "Görsel sisteme kaydedilirken hata oluştu: " + e.getMessage();
            }
        } else {
            // resim kaldırılıyorsa dosyayı sil
            try {
                java.io.File f = new java.io.File("assets-avatars/avatar_" + userId + ".png");
                if (f.exists()) f.delete();
            } catch (Exception ignored) {}
        }
        
        boolean success = userDAO.updateProfilePicture(userId, imagePath);
        if (success) {
            return null;
        }
        return "Profil resmi güncellenirken veritabanı hatası oluştu.";
    }
}