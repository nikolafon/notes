package com.example.notes.resource;

import lombok.Getter;

/**
 * Enum representing the roles that can be assigned to a user.
 */
@Getter
public enum Role {
    SUPER_ADMIN("super_admin"), ADMIN("admin"), USER("user");

    Role(String role) {
        this.role = role;
    }

    private final String role;

}
