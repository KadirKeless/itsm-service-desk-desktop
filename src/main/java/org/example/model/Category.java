package org.example.model;

public class Category {
    private int id;
    private String categoryName;
    private int departmentId;

    public Category() {
    }

    public Category(int id, String categoryName, int departmentId) {
        this.id = id;
        this.categoryName = categoryName;
        this.departmentId = departmentId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getDepartmentId() { return departmentId; }
    public void setDepartmentId(int departmentId) { this.departmentId = departmentId; }

    @Override
    public String toString() {
        return categoryName;
    }
}
