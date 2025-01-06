package com.example.notes.init;

import com.example.notes.resource.Role;
import com.example.notes.resource.Tenant;
import com.example.notes.resource.User;
import com.example.notes.service.TenantService;
import com.example.notes.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Initializes the database with default data.
 */
@Component
public class InitDb {

    public static final String SUPER_ADMIN = "super_admin";
    public static final String ADMIN = "admin";
    @Autowired
    private UserService userService;
    @Autowired
    private TenantService tenantService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    //@Transactional
    public void init() {
        Optional<User> optionalUser = userService.find(String.format("{username: '%s'}", SUPER_ADMIN)).stream().findFirst();
        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setFirstName("Nikola");
            user.setLastName("Nikolic");
            user.setUsername(SUPER_ADMIN);
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("nikola.nikolic@outlook.com");
            user.setAuthorities(Set.of(new SimpleGrantedAuthority(Role.SUPER_ADMIN.getRole())));
            userService.create(user);
        }
        String defaultTenantId = "default";
        Optional<Tenant> optionalTenant = tenantService.find(String.format("{tenantId: '%s'}", defaultTenantId)).stream().findFirst();
        if (optionalTenant.isEmpty()) {
            Tenant tenant = new Tenant();
            tenant.setTenantId(defaultTenantId);
            tenant.setName("Default");
            tenantService.create(tenant);
        }
        Optional<User> optionalTenantUser = userService.find(String.format("{username: '%s', tenantId: '%s'}", ADMIN, defaultTenantId)).stream().findFirst();
        if (optionalTenantUser.isEmpty()) {
            User user = new User();
            user.setFirstName("Nikola");
            user.setLastName("Nikolic");
            user.setUsername(ADMIN);
            user.setTenantId(defaultTenantId);
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("nikola.nikolic@outlook.com");
            user.setAuthorities(Set.of(new SimpleGrantedAuthority(Role.ADMIN.getRole())));
            userService.create(user);
        }

    }
}
