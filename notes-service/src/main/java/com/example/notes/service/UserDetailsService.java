package com.example.notes.service;

import com.example.notes.tenant.TenantHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String tenantId = TenantHolder.getCurrentTenantId();
        return userService.find(String.format("{ username: '%s', tenantId: %s }", username, tenantId != null ? String.format("'%s'", tenantId) : null)).stream().findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
