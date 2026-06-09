package org.example.service;

import org.example.dao.TicketReplyDAO;
import org.example.model.TicketReply;

import java.util.List;

public class TicketReplyService {

    private TicketReplyDAO ticketReplyDAO;

    public TicketReplyService() {
        this.ticketReplyDAO = new TicketReplyDAO();
    }

    public String addReply(int ticketId, int userId, String message) {

        // mesaj min 3 karakter
        if (message == null || message.trim().length() < 3) {
            return "Yanıt mesajı en az 3 karakter olmalıdır!";
        }

        TicketReply reply = new TicketReply(ticketId, userId, message.trim());

        boolean isSaved = ticketReplyDAO.addReply(reply);

        if (isSaved) {
            System.out.println("[BAŞARILI] Yanıt eklendi.");
            return null;
        } else {
            return "Yanıt eklenirken veritabanı hatası oluştu.";
        }
    }

    public List<TicketReply> getRepliesByTicketId(int ticketId) {
        return ticketReplyDAO.getRepliesByTicketId(ticketId);
    }

    public String updateReply(int replyId, String newMessage) {
        if (newMessage == null || newMessage.trim().length() < 3) {
            return "Yanıt mesajı en az 3 karakter olmalıdır!";
        }
        boolean isUpdated = ticketReplyDAO.updateReply(replyId, newMessage.trim());
        if (isUpdated) {
            System.out.println("[BAŞARILI] Yanıt güncellendi.");
            return null;
        } else {
            return "Yanıt güncellenirken veritabanı hatası oluştu.";
        }
    }

    public String deleteReply(int replyId) {
        boolean isDeleted = ticketReplyDAO.deleteReply(replyId);
        if (isDeleted) {
            System.out.println("[BAŞARILI] Yanıt silindi.");
            return null;
        } else {
            return "Yanıt silinirken veritabanı hatası oluştu.";
        }
    }
}
