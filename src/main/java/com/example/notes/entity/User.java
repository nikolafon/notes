package com.example.notes.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Encrypted;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "#{@tenantCollectionNameResolver.getTenantCollectionName('users')}")
public class User extends BaseEntity {
    @NotEmpty
    @Size(max = 50)
    private String firstName;
    @NotEmpty
    @Size(max = 50)
    private String lastName;
    @Email
    @NotEmpty
    private String email;
    @NotEmpty
    @Size(max = 20)
    private String userName;
    @NotEmpty
    @Size(max = 50)
    @Encrypted
    private String password;
}
