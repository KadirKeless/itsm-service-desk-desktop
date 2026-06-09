package org.example.model;

import java.sql.Timestamp;

public class Ticket {
    private int id;
    private String title;
    private String description;

    private int requesterUserId;
    private int targetDepartmentId;
    private int categoryId;
    private int priorityId;
    private int statusId;

    private Integer assignedUserId;

    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp closedAt;

    public Ticket() {
    }

    // yeni ticket — id/tarih/atanan DB'de
    public Ticket(String title, String description, int requesterUserId, int targetDepartmentId, int categoryId, int priorityId) {
        this.title = title;
        this.description = description;
        this.requesterUserId = requesterUserId;
        this.targetDepartmentId = targetDepartmentId;
        this.categoryId = categoryId;
        this.priorityId = priorityId;
    }

    public Ticket(int id, String title, String description, int requesterUserId, int targetDepartmentId,
                  int categoryId, int priorityId, int statusId, Integer assignedUserId,
                  Timestamp createdAt, Timestamp updatedAt, Timestamp closedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requesterUserId = requesterUserId;
        this.targetDepartmentId = targetDepartmentId;
        this.categoryId = categoryId;
        this.priorityId = priorityId;
        this.statusId = statusId;
        this.assignedUserId = assignedUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.closedAt = closedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getRequesterUserId() { return requesterUserId; }
    public void setRequesterUserId(int requesterUserId) { this.requesterUserId = requesterUserId; }

    public int getTargetDepartmentId() { return targetDepartmentId; }
    public void setTargetDepartmentId(int targetDepartmentId) { this.targetDepartmentId = targetDepartmentId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getPriorityId() { return priorityId; }
    public void setPriorityId(int priorityId) { this.priorityId = priorityId; }

    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }

    public Integer getAssignedUserId() { return assignedUserId; }
    public void setAssignedUserId(Integer assignedUserId) { this.assignedUserId = assignedUserId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Timestamp getClosedAt() { return closedAt; }
    public void setClosedAt(Timestamp closedAt) { this.closedAt = closedAt; }
}