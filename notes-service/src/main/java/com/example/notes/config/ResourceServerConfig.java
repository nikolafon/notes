package com.example.notes.config;

import com.example.notes.filter.CorsFilter;
import com.example.notes.filter.RateLimitFilter;
import com.example.notes.filter.TenantFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
public class ResourceServerConfig {
    public static final String SCOPE_USER = "SCOPE_user";
    public static final String SCOPE_ADMIN = "SCOPE_admin";
    public static final String SCOPE_SUPERADMIN = "SCOPE_super_admin";
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TenantFilter tenantFilter;
    @Autowired
    private CorsFilter corsFilter;
    @Autowired
    private RateLimitFilter rateLimitFilter;

    @Bean
    @Order(3)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .addFilterBefore(tenantFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterAfter(rateLimitFilter, TenantFilter.class)
                .authorizeHttpRequests(authorizeRequests -> {
                            authorizeRequests
                                    .requestMatchers(HttpMethod.GET, "/api/notes/**").hasAnyAuthority(SCOPE_USER, SCOPE_ADMIN, SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.POST, "/api/notes/**").hasAnyAuthority(SCOPE_USER, SCOPE_ADMIN, SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.PUT, "/api/notes/**").hasAnyAuthority(SCOPE_USER, SCOPE_ADMIN, SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.DELETE, "/api/notes/**").hasAnyAuthority(SCOPE_USER, SCOPE_ADMIN, SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority(SCOPE_ADMIN, SCOPE_USER, SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.POST, "/api/users/**").hasAnyAuthority(SCOPE_ADMIN, SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyAuthority(SCOPE_ADMIN, SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyAuthority(SCOPE_ADMIN, SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.GET, "/api/tenants/**").hasAnyAuthority(SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.POST, "/api/tenants/**").hasAnyAuthority(SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.PUT, "/api/tenants/**").hasAnyAuthority(SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.DELETE, "/api/tenants/**").hasAnyAuthority(SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.GET, "/api/resourceaudits/**").hasAnyAuthority(SCOPE_SUPERADMIN)
                                    .requestMatchers(HttpMethod.POST, "/api/resourceaudits/**").denyAll()
                                    .requestMatchers(HttpMethod.PUT, "/api/resourceaudits/**").denyAll()
                                    .requestMatchers(HttpMethod.DELETE, "/api/resourceaudits/**").denyAll()
                                    .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll();
                            authorizeRequests
                                    .anyRequest().authenticated();
                        }
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtCustomizer -> {
                }))
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

}
