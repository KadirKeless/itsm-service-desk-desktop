package org.example.model;

public class TicketStatus {
    private int id;
    private String statusName;

    public TicketStatus() {
    }

    public TicketStatus(int id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    // --- GETTER & SETTER ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    @Override
    public String toString() {
        return statusName;
    }
}
