package com.example.notes.init;

import com.example.notes.repository.TenantRepository;
import com.example.notes.repository.UserRepository;
import com.example.notes.resource.Role;
import com.example.notes.resource.Tenant;
import com.example.notes.resource.User;
import com.example.notes.tenant.TenantHolder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
    private UserRepository userRepository;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        User exampleUser = new User();
        exampleUser.setUsername(SUPER_ADMIN);
        Optional<User> optionalUser = userRepository.findOne(Example.of(exampleUser));
        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setFirstName("Nikola");
            user.setLastName("Nikolic");
            user.setUsername(SUPER_ADMIN);
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("nikola.nikolic@outlook.com");
            user.setAuthorities(Set.of(new SimpleGrantedAuthority(Role.SUPER_ADMIN.getRole())));
            userRepository.save(user);
        }
        Tenant exampleTenant = new Tenant();
        String defaultTenantId = "default";
        exampleTenant.setTenantId(defaultTenantId);
        Optional<Tenant> optionalTenant = tenantRepository.findOne(Example.of(exampleTenant));
        if (optionalTenant.isEmpty()) {
            Tenant tenant = new Tenant();
            tenant.setTenantId(defaultTenantId);
            tenant.setName("Default");
            tenantRepository.save(tenant);
        }
        TenantHolder.setCurrentTenantId(defaultTenantId);
        try {
            User exampleTenantUser = new User();
            exampleTenantUser.setUsername(ADMIN);
            Optional<User> optionalTenantUser = userRepository.findOne(Example.of(exampleTenantUser));
            if (optionalTenantUser.isEmpty()) {
                User user = new User();
                user.setFirstName("Nikola");
                user.setLastName("Nikolic");
                user.setUsername(ADMIN);
                user.setPassword(passwordEncoder.encode("admin"));
                user.setEmail("nikola.nikolic@outlook.com");
                user.setAuthorities(Set.of(new SimpleGrantedAuthority(Role.ADMIN.getRole())));
                userRepository.save(user);
            }
        } finally {
            TenantHolder.clear();
        }

    }
}
