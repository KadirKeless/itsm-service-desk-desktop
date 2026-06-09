package org.example.service;

import org.example.dao.TicketDAO;
import org.example.dao.UserDAO;
import org.example.model.Ticket;
import org.example.model.User;
import org.example.utils.ValidationUtils;

import java.util.List;

public class TicketService {

    private TicketDAO ticketDAO;
    private UserDAO userDAO;

    public TicketService() {
        this.ticketDAO = new TicketDAO();
        this.userDAO = new UserDAO();
    }

    // hata yoksa null döner
    public String createTicket(String title, String description, int requesterId, int departmentId, int categoryId, int priorityId) {

        // kendi dep'ına ticket yok
        User requester = new org.example.dao.UserDAO().getUserById(requesterId);
        if (requester != null && requester.getDepartmentId() != null
                && requester.getDepartmentId() == departmentId) {
            return "Kendi departmanınıza talep açamazsınız!";
        }

        String titleErr = ValidationUtils.validateTicketTitle(title);
        if (titleErr != null) return titleErr;

        String descErr = ValidationUtils.validateTicketDescription(description);
        if (descErr != null) return descErr;

        // combobox'lar seçilmiş olmalı (>0)
        if (departmentId <= 0 || categoryId <= 0 || priorityId <= 0) {
            return "Departman, Kategori ve Öncelik seçimleri eksiksiz yapılmalıdır!";
        }

        Ticket newTicket = new Ticket(title, description, requesterId, departmentId, categoryId, priorityId);

        boolean isCreated = ticketDAO.createTicket(newTicket);

        if (isCreated) {
            System.out.println("[BAŞARILI] Destek talebiniz oluşturuldu.");
            return null;
        } else {
            return "Talep oluşturulurken veritabanı hatası meydana geldi.";
        }
    }

    public Ticket getTicketById(int id) {
        return ticketDAO.getTicketById(id);
    }

    public List<Ticket> getTicketsByRequesterId(int requesterId) {
        return ticketDAO.getTicketsByRequesterId(requesterId);
    }

    public List<Ticket> getTicketsByDepartmentId(int departmentId) {
        return ticketDAO.getTicketsByDepartmentId(departmentId);
    }

    public List<Ticket> getAllTickets() {
        return ticketDAO.getAllTickets();
    }

    public List<Ticket> getTicketsByAssignedUserId(int assignedUserId) {
        return ticketDAO.getTicketsByAssignedUserId(assignedUserId);
    }

    public String updateTicketStatus(int ticketId, int statusId) {

        // status 1..4
        if (statusId < 1 || statusId > 4) {
            return "Geçersiz durum seçimi!";
        }

        Ticket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            return "Talep bulunamadı!";
        }

        if (ticket.getStatusId() == 3 || ticket.getStatusId() == 4) {
            return "Çözülmüş veya İptal Edilmiş bir talep tekrar güncellenemez!";
        }

        // kapatılıyorsa closed_at DAO'da
        if (statusId == 3 || statusId == 4) {
            boolean isClosed = ticketDAO.closeTicket(ticketId, statusId);
            return isClosed ? null : "Talep kapatılırken veritabanı hatası oluştu.";
        }

        boolean isUpdated = ticketDAO.updateTicketStatus(ticketId, statusId);
        return isUpdated ? null : "Talep durumu güncellenirken veritabanı hatası oluştu.";
    }

    public String assignTicket(int ticketId, int assignedUserId) {

        Ticket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            return "Talep bulunamadı!";
        }

        if (ticket.getStatusId() == 3 || ticket.getStatusId() == 4) {
            return "Çözülmüş veya İptal Edilmiş bir talebe personel atanamaz!";
        }

        User assignee = userDAO.getUserById(assignedUserId);
        if (assignee == null) {
            return "Atanacak kullanıcı bulunamadı!";
        }
        if (!assignee.isApproved()) {
            return "Dondurulmuş veya onay bekleyen bir kullanıcıya talep atanamaz!";
        }

        boolean isAssigned = ticketDAO.assignTicket(ticketId, assignedUserId);
        return isAssigned ? null : "Talep atanırken veritabanı hatası oluştu.";
    }

    public String deleteTicket(int ticketId) {
        Ticket ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) return "Talep bulunamadı!";
        boolean ok = ticketDAO.deleteTicket(ticketId);
        return ok ? null : "Talep silinemedi. Lütfen tekrar deneyiniz.";
    }
}