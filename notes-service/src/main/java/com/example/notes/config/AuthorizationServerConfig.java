package com.example.notes.config;

import com.example.notes.filter.CorsFilter;
import com.example.notes.filter.TenantFilter;
import com.example.notes.repository.ResourceRepository;
import com.example.notes.resource.User;
import com.example.notes.service.UserDetailsService;
import com.example.notes.tenant.TenantHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpSession;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
public class AuthorizationServerConfig {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ResourceRepository resourceRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TenantFilter tenantFilter;
    @Autowired
    private CustomOAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver;
    @Autowired
    private HttpSession httpSession;

    @Value("${webapp.redirectUri}")
    private String webappRedirectUri;
    @Autowired
    private CorsFilter corsFilter;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JWKSource<SecurityContext> jwkSource;

    @Bean
    @Order(1)
    public SecurityFilterChain loginSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .securityMatcher("/login/**", "/logout/**", "/login/oauth2/**", "/default-ui.css", "/oauth2/authorization/**")
                .addFilterBefore(tenantFilter, AbstractPreAuthenticatedProcessingFilter.class)
                .formLogin(formLogin -> {
                            formLogin
                                    .successHandler((request, response, authentication) -> response.setStatus(HttpStatus.SC_OK));
                            formLogin
                                    .failureHandler((request, response, authentication) -> response.setStatus(HttpStatus.SC_BAD_REQUEST));
                        }
                )
                .oauth2Login(oauth2Login -> {
                            oauth2Login.authorizationEndpoint(authorizationEndpoint ->
                                    authorizationEndpoint.authorizationRequestResolver(customOAuth2AuthorizationRequestResolver)
                            );
                            oauth2Login
                                    .userInfoEndpoint(userInfoEndpoint ->
                                            userInfoEndpoint
                                                    .userService(oauth2UserService())
                                    );

                            oauth2Login.successHandler((request, response, authentication) -> {
                                response.sendRedirect(request.getHeader("referer").substring(0, request.getHeader("referer").lastIndexOf("/")));
                            });
                        }
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();
        http.addFilterAfter(corsFilter, AbstractPreAuthenticatedProcessingFilter.class).
                securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) -> {
                            authorizationServer
                                    .oidc(Customizer.withDefaults());
                        }

                )
                .csrf(AbstractHttpConfigurer::disable);
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
                resourceRepository.create(user);
                return new DefaultOAuth2User(user.getAuthorities(), oauth2User.getAttributes(), "login");
            } finally {
                TenantHolder.clear();
            }
        };
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient notesWebApp = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("notes-webapp")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(webappRedirectUri)
                .postLogoutRedirectUri(webappRedirectUri)
                .scope(OidcScopes
                        .PROFILE)
                .scope(OidcScopes.OPENID)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .requireProofKey(true).build())
                .build();

        RegisteredClient postman = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("postman")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .scope(OidcScopes.OPENID).tokenSettings(
                        TokenSettings.builder().refreshTokenTimeToLive(Duration.ofDays(1)).build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .requireProofKey(false).build())

                .build();
        return new InMemoryRegisteredClientRepository(notesWebApp, postman);
    }


    @Bean
    public OAuth2TokenGenerator<OAuth2Token> tokenGenerator() {
        var jwtEncoder = new NimbusJwtEncoder(jwkSource);
        var jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(oAuth2TokenCustomizer());
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        var refreshTokenGenerator = new CustomRefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }

    private OAuth2TokenCustomizer<JwtEncodingContext> oAuth2TokenCustomizer() {
        return context -> {
            Set<String> roles = AuthorityUtils.authorityListToSet(context.getPrincipal().getAuthorities())
                    .stream()
                    .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
            if (TenantHolder.getCurrentTenantId() != null) {
                context.getClaims().claim("tenant", TenantHolder.getCurrentTenantId());
            }
            context.getClaims().claim("authorities", roles);
        };
    }

    private static final class CustomRefreshTokenGenerator implements OAuth2TokenGenerator<OAuth2RefreshToken> {
        private final StringKeyGenerator refreshTokenGenerator =
                new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);

        @Nullable
        @Override
        public OAuth2RefreshToken generate(OAuth2TokenContext context) {
            if (!OAuth2TokenType.REFRESH_TOKEN.equals(context.getTokenType())) {
                return null;
            }
            Instant issuedAt = Instant.now();
            Instant expiresAt = issuedAt.plus(context.getRegisteredClient().getTokenSettings().getRefreshTokenTimeToLive());
            return new OAuth2RefreshToken(this.refreshTokenGenerator.generateKey(), issuedAt, expiresAt);
        }

    }

}
