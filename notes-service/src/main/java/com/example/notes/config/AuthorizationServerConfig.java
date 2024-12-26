package com.example.notes.config;

import com.example.notes.filter.TenantFilter;
import com.example.notes.repository.UserRepository;
import com.example.notes.resource.User;
import com.example.notes.service.MongoDbUserDetailsService;
import com.example.notes.tenant.TenantHolder;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
public class AuthorizationServerConfig {

    @Autowired
    private MongoDbUserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TenantFilter tenantFilter;
    @Autowired
    private CustomOAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver;
    @Autowired
    private HttpSession httpSession;

    @Bean
    @Order(1)
    public SecurityFilterChain loginSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .securityMatcher("/login/**", "/logout/**", "/login/oauth2/**", "/default-ui.css", "/oauth2/authorization/**")
                .addFilterBefore(tenantFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .formLogin(Customizer.withDefaults())
                .oauth2Login(oauth2Login -> {
                            oauth2Login.authorizationEndpoint(authorizationEndpoint ->
                                    authorizationEndpoint.authorizationRequestResolver(customOAuth2AuthorizationRequestResolver)
                            );
                            oauth2Login
                                    .userInfoEndpoint(userInfoEndpoint ->
                                            userInfoEndpoint
                                                    .userService(oauth2UserService())
                                    );
                        }
                )
                .cors(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();
        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .oidc(Customizer.withDefaults())
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());
        return http.build();
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User oauth2User = delegate.loadUser(request);
            String username = oauth2User.getAttribute("login");
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String firstName = name.split(" ")[0];
            String lastName = name.split(" ")[1];
            String tenant = (String) httpSession.getAttribute("tenantId");
            try {
                if (tenant != null) {
                    TenantHolder.setCurrentTenantId(tenant);
                }
                return new DefaultOAuth2User(userDetailsService.loadUserByUsername(username).getAuthorities(), oauth2User.getAttributes(), "login");
            } catch (UsernameNotFoundException e) {
                User user = new User();
                user.setUsername(username);
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                user.setAuthorities(Set.of(new SimpleGrantedAuthority("user")));
                userRepository.save(user);
                return new DefaultOAuth2User(user.getAuthorities(), oauth2User.getAttributes(), "login");
            } finally {
                TenantHolder.clear();
            }
        };
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> oAuth2TokenCustomizer() {
        return context -> {
            Set<String> roles = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                    .stream()
                    .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
            context.getClaims().claim("tenant", TenantHolder.getCurrentTenantId());
            context.getClaims().claim("authorities", roles);
        };
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOrigin("http://localhost:4200");
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("web-client")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:4200/login-callback")
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .postLogoutRedirectUri("http://localhost:8080/login")
                .scope(OidcScopes
                        .PROFILE)
                .scope(OidcScopes.OPENID)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .requireProofKey(false).build())
                .build();
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

}
