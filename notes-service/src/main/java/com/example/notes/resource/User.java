package com.example.notes.resource;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "#{@tenantCollectionNameResolver.getTenantCollectionName('users')}")
public class User extends BaseResource implements UserDetails {
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
    @Indexed(unique = true)
    private String username;
    @NotEmpty
    @Size(max = 50)
    private String password;
    private Set<SimpleGrantedAuthority> authorities;
}
