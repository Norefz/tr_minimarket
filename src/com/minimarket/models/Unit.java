package com.minimarket.models;

import java.time.LocalDateTime;

public class Unit {
    private int id;
    private String name;
    private String abbreviation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Unit() {}

    public Unit(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAbbreviation() { return abbreviation; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return name + " (" + abbreviation + ")";
    }
}