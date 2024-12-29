package com.example.notes.resource;

public enum Role {
    SUPER_ADMIN("super_admin"), ADMIN("admin"), USER("user");

    Role(String role) {
        this.role = role;
    }

    private final String role;

    public String getRole() {
        return role;
    }
}
