package com.example.notes.resource;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * User resource.
 */
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@Data
@Document(collection = "users")
@CompoundIndex(def = "{'tenantId':1,'username':1}", unique = true)
public class User extends BaseResource implements UserDetails {
    @Indexed
    private String tenantId;
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
    @Indexed
    private String username;
    @NotEmpty
    @Size(max = 50)
    private String password;
    private Set<SimpleGrantedAuthority> authorities;
}
