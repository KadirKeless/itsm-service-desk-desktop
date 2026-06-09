package org.example.service;

import org.example.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceIntegrationTest {




    @Test
    @DisplayName("Senaryo 1: Yeni Kullanıcı Kaydı ve Veritabanı Kalıcılık Testi")
    public void testRegisterUserAndKeepInDatabase() {
        UserService service = new UserService();

        long timestamp = System.currentTimeMillis();
        String testEmail = "gozlem_testi_" + timestamp + "@isikun.com";

        try {
            boolean isRegistered = service.register("Gözlem", "Testi", testEmail, "Gecerli!123");
            assertTrue(isRegistered, "Kullanıcı veritabanına başarıyla kaydedilmelidir.");

            User savedUser = service.getUserByEmail(testEmail);
            assertNotNull(savedUser, "Kaydedilen kullanıcı veritabanında bulunabilmelidir.");
            assertFalse(savedUser.isApproved(), "Yeni kullanıcı varsayılan olarak onaysız (0) olmalıdır.");

            System.out.println("Kayıt Testi Başarılı! Veritabanında kontrol edilecek mail: " + testEmail);

        } catch (Exception e) {
            fail("Beklenmeyen bir hata oluştu: " + e.getMessage());
        }
    }


    @Test
    @DisplayName("Senaryo 2: Mantıksal Eşleşme Testi (BT -> İK / İşe Alım)")
    public void testCreateTicketWithLogicalMatch() {
        TicketService ticketService = new TicketService();

        long timestamp = System.currentTimeMillis();
        String ticketTitle = "[OTOMASYON TESTİ] - " + timestamp;
        String ticketDesc = "Bilgi Teknolojileri birimine alınacak yeni yazılımcı için işe alım talebidir.";

        try {


            String result = ticketService.createTicket(ticketTitle, ticketDesc, 1, 2, 11, 1);


            assertNull(result, "Bilet oluşturulurken hata alındı: " + result);

            System.out.println("Eşleşme Başarılı! 'İK' departmanına 'İşe Alım' kategorisinde bilet açıldı.");

        } catch (Exception e) {
            fail("Mantıksal eşleşme testinde beklenmeyen hata: " + e.getMessage());
        }
    }
}