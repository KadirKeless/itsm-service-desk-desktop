package org.example.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TicketReplyServiceTest {

    @Test
    public void testAddReplyTooShortMessage() {
        TicketReplyService service = new TicketReplyService();
        String err = service.addReply(1, 1, "ab");
        assertEquals("Yanıt mesajı en az 3 karakter olmalıdır!", err);
    }

    @Test
    public void testAddReplyNullMessage() {
        TicketReplyService service = new TicketReplyService();
        String err = service.addReply(1, 1, null);
        assertEquals("Yanıt mesajı en az 3 karakter olmalıdır!", err);
    }

    @Test
    public void testAddReplyWhitespaceOnly() {
        TicketReplyService service = new TicketReplyService();
        String err = service.addReply(1, 1, "  ");
        assertEquals("Yanıt mesajı en az 3 karakter olmalıdır!", err);
    }

    @Test
    public void testUpdateReplyTooShort() {
        TicketReplyService service = new TicketReplyService();
        String err = service.updateReply(1, "xx");
        assertEquals("Yanıt mesajı en az 3 karakter olmalıdır!", err);
    }
}
