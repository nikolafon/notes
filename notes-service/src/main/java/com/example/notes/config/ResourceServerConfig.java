package com.example.notes.config;

import com.example.notes.filter.RateLimitFilter;
import com.example.notes.filter.TenantFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
public class ResourceServerConfig {
    public static final String ROLE_USER = "SCOPE_user";
    public static final String ROLE_ADMIN = "SCOPE_admin";
    public static final String ROLE_SUPERUSER = "SCOPE_superuser";
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TenantFilter tenantFilter;
    @Autowired
    private RateLimitFilter rateLimitFilter;

    @Bean
    @Order(3)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .addFilterBefore(tenantFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterBefore(rateLimitFilter, TenantFilter.class)
                .authorizeHttpRequests(authorizeRequests -> {
                            authorizeRequests
                                    .requestMatchers(HttpMethod.GET, "/api/notes/**").hasAnyAuthority(ROLE_USER, ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.POST, "/api/notes/**").hasAnyAuthority(ROLE_USER, ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.PUT, "/api/notes/**").hasAnyAuthority(ROLE_USER, ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.DELETE, "/api/notes/**").hasAnyAuthority(ROLE_USER, ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority(ROLE_ADMIN, ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.POST, "/api/users/**").hasAnyAuthority(ROLE_ADMIN, ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyAuthority(ROLE_ADMIN, ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyAuthority(ROLE_ADMIN, ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.GET, "/api/tenants/**").hasAnyAuthority(ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.POST, "/api/tenants/**").hasAnyAuthority(ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.PUT, "/api/tenants/**").hasAnyAuthority(ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.DELETE, "/api/tenants/**").hasAnyAuthority(ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.GET, "/api/resourceaudits/**").hasAnyAuthority(ROLE_SUPERUSER)
                                    .requestMatchers(HttpMethod.POST, "/api/resourceaudits/**").denyAll()
                                    .requestMatchers(HttpMethod.PUT, "/api/resourceaudits/**").denyAll()
                                    .requestMatchers(HttpMethod.DELETE, "/api/resourceaudits/**").denyAll();
                            authorizeRequests
                                    .anyRequest().authenticated();
                        }
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtCustomizer -> {
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
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
