package com.example.notes.config;

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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TenantFilter tenantFilter;

    @Bean
    @Order(3)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**")
                .addFilterBefore(tenantFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .authorizeHttpRequests(authorizeRequests -> {
                            authorizeRequests
                                    .requestMatchers(HttpMethod.GET, "/api/notes/**").hasAuthority(ROLE_USER)
                                    .requestMatchers(HttpMethod.POST, "/api/notes/**").hasAuthority(ROLE_USER)
                                    .requestMatchers(HttpMethod.PUT, "/api/notes/**").hasAuthority(ROLE_USER)
                                    .requestMatchers(HttpMethod.DELETE, "/api/notes/**").hasAuthority(ROLE_USER)
                                    .requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority(ROLE_ADMIN)
                                    .requestMatchers(HttpMethod.POST, "/api/users/**").hasAuthority(ROLE_ADMIN)
                                    .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority(ROLE_ADMIN)
                                    .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority(ROLE_ADMIN)
                                    .requestMatchers(HttpMethod.GET, "/api/tenants/**").permitAll()
                                    .requestMatchers(HttpMethod.POST, "/api/tenants/**").hasAuthority(ROLE_ADMIN)
                                    .requestMatchers(HttpMethod.PUT, "/api/tenants/**").hasAuthority(ROLE_ADMIN)
                                    .requestMatchers(HttpMethod.DELETE, "/api/tenants/**").hasAuthority(ROLE_ADMIN);
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
