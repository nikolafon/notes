package com.example.notes.resource;

public enum Role {
    SUPER_ADMIN("SUPER_ADMIN"), ADMIN("ADMIN"), USER("USER");

    Role(String role) {
        this.role = role;
    }

    private final String role;

    public String getRole() {
        return role;
    }
}
