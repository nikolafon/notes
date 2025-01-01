package com.example.notes.init;

import com.example.notes.repository.ResourceRepository;
import com.example.notes.resource.Role;
import com.example.notes.resource.Tenant;
import com.example.notes.resource.User;
import com.example.notes.tenant.TenantHolder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class InitDb {

    public static final String SUPER_ADMIN = "super_admin";
    public static final String ADMIN = "admin";
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        Optional<User> optionalUser = resourceRepository.find(new BasicQuery(String.format("{username: '%s'}", SUPER_ADMIN)), User.class).stream().findFirst();
        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setFirstName("Nikola");
            user.setLastName("Nikolic");
            user.setUsername(SUPER_ADMIN);
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("nikola.nikolic@outlook.com");
            user.setAuthorities(Set.of(new SimpleGrantedAuthority(Role.SUPER_ADMIN.getRole())));
            resourceRepository.create(user);
        }
        String defaultTenantId = "default";
        Optional<Tenant> optionalTenant = resourceRepository.find(new BasicQuery(String.format("{tenantId: '%s'}", defaultTenantId)), Tenant.class).stream().findFirst();
        if (optionalTenant.isEmpty()) {
            Tenant tenant = new Tenant();
            tenant.setTenantId(defaultTenantId);
            tenant.setName("Default");
            resourceRepository.create(tenant);
        }
        TenantHolder.setCurrentTenantId(defaultTenantId);
        try {
            Optional<User> optionalTenantUser = resourceRepository.find(new BasicQuery(String.format("{username: '%s'}", ADMIN)), User.class).stream().findFirst();
            if (optionalTenantUser.isEmpty()) {
                User user = new User();
                user.setFirstName("Nikola");
                user.setLastName("Nikolic");
                user.setUsername(ADMIN);
                user.setPassword(passwordEncoder.encode("admin"));
                user.setEmail("nikola.nikolic@outlook.com");
                user.setAuthorities(Set.of(new SimpleGrantedAuthority(Role.ADMIN.getRole())));
                resourceRepository.create(user);
            }
        } finally {
            TenantHolder.clear();
        }

    }
}
