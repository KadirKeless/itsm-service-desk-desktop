package org.example.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TicketServiceTest {

    @Test
    public void testUpdateTicketStatusInvalidStatusIdZero() {
        TicketService service = new TicketService();
        String err = service.updateTicketStatus(1, 0);
        assertEquals("Geçersiz durum seçimi!", err);
    }

    @Test
    public void testUpdateTicketStatusInvalidStatusIdTooHigh() {
        TicketService service = new TicketService();
        String err = service.updateTicketStatus(1, 5);
        assertEquals("Geçersiz durum seçimi!", err);
    }

    @Test
    public void testUpdateTicketStatusInvalidStatusNegative() {
        TicketService service = new TicketService();
        String err = service.updateTicketStatus(99, -1);
        assertEquals("Geçersiz durum seçimi!", err);
    }
}
