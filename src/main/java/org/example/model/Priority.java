package org.example.model;

public class Priority {
    private int id;
    private String priorityName;
    private int levelWeight;

    public Priority() {
    }

    public Priority(int id, String priorityName, int levelWeight) {
        this.id = id;
        this.priorityName = priorityName;
        this.levelWeight = levelWeight;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPriorityName() { return priorityName; }
    public void setPriorityName(String priorityName) { this.priorityName = priorityName; }

    public int getLevelWeight() { return levelWeight; }
    public void setLevelWeight(int levelWeight) { this.levelWeight = levelWeight; }

    @Override
    public String toString() {
        return priorityName;
    }
}
