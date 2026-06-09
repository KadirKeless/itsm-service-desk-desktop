package org.example.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// servis testleri — DB'ye gitmeden kesilen yollar
public class UserServiceTest {

    @Test
    public void testRegisterFailsWhenFirstNameEmpty() {
        UserService service = new UserService();
        boolean result = service.register("", "Soyad", "ad@isikun.com", "Valid1!pass");
        assertFalse(result, "Ad boşken kayıt yapılmamalı.");
    }

    @Test
    public void testRegisterFailsWhenLastNameEmpty() {
        UserService service = new UserService();
        assertFalse(service.register("Ad", "", "ad@isikun.com", "Valid1!pass"));
    }

    @Test
    public void testRegisterFailsWhenEmailInvalid() {
        UserService service = new UserService();
        boolean result = service.register("Ad", "Soyad", "gecersiz-email", "Valid1!pass");
        assertFalse(result, "Geçersiz e-posta ile kayıt olmamalı.");
    }

    @Test
    public void testRegisterFailsWhenPasswordWeak() {
        UserService service = new UserService();
        boolean result = service.register("Ad", "Soyad", "ad@isikun.com", "123");
        assertFalse(result, "Zayıf şifre ile kayıt olmamalı.");
    }

    @Test
    public void testLoginReturnsNullWhenEmailEmpty() {
        UserService service = new UserService();
        assertNull(service.login("", "Valid1!pass"), "Boş e-posta ile giriş null dönmeli.");
    }

    @Test
    public void testLoginReturnsNullWhenPasswordEmpty() {
        UserService service = new UserService();
        assertNull(service.login("ad@isikun.com", ""), "Boş şifre ile giriş null dönmeli.");
    }

    @Test
    public void testLoginReturnsNullWhenBothEmpty() {
        UserService service = new UserService();
        assertNull(service.login("  ", "  "));
    }

    @Test
    public void testAdminCreateUserFailsWhenNamesEmpty() {
        UserService service = new UserService();
        assertEquals("Ad ve soyad alanları boş bırakılamaz!",
                service.adminCreateUser("", "Soyad", "a@b.com", "secret1", 2, 1));
    }

    @Test
    public void testAdminCreateUserFailsWhenEmailInvalid() {
        UserService service = new UserService();
        assertEquals("Geçerli bir e-posta adresi giriniz!",
                service.adminCreateUser("Ad", "Soyad", "bad", "secret1", 2, 1));
    }

    @Test
    public void testAdminCreateUserFailsWhenPasswordShort() {
        UserService service = new UserService();
        assertEquals("Şifre en az 6 karakter olmalıdır!",
                service.adminCreateUser("Ad", "Soyad", "a@b.com", "12345", 2, 1));
    }

    @Test
    public void testAdminCreateUserFailsWhenRoleNull() {
        UserService service = new UserService();
        assertEquals("Lütfen atama için bir rol seçiniz!",
                service.adminCreateUser("Ad", "Soyad", "a@b.com", "secret1", null, 1));
    }

    @Test
    public void testAdminCreateUserFailsWhenDepartmentNull() {
        UserService service = new UserService();
        assertEquals("Lütfen atama için departman seçiniz!",
                service.adminCreateUser("Ad", "Soyad", "a@b.com", "secret1", 2, null));
    }

    @Test
    public void testAdminCreateUserFailsWhenRoleIsAdmin() {
        UserService service = new UserService();
        assertEquals("Admin rolü yeni kullanıcılara atanamaz!",
                service.adminCreateUser("Ad", "Soyad", "a@b.com", "secret1", 1, 1));
    }

    @Test
    public void testUpdateProfilePictureFailsWhenFileTooLarge() {
        UserService service = new UserService();
        byte[] huge = new byte[5 * 1024 * 1024 + 1];
        String err = service.updateProfilePicture(1, huge);
        assertNotNull(err);
        assertTrue(err.contains("5 MB"), "Beklenen boyut uyarısı.");
    }
}
