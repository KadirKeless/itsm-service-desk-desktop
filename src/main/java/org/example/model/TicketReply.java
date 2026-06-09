package org.example.model;

import java.sql.Timestamp;

public class TicketReply {
    private int id;
    private int ticketId;
    private int userId;
    private String message;
    private Timestamp createdAt;

    public TicketReply() {
    }

    public TicketReply(int ticketId, int userId, String message) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.message = message;
    }

    public TicketReply(int id, int ticketId, int userId, String message, Timestamp createdAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
