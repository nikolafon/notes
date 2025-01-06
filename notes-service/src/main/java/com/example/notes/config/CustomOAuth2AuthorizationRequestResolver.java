package com.example.notes.config;

import com.example.notes.tenant.TenantHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
    private final OAuth2AuthorizationRequestResolver defaultResolver;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HttpSession httpSession;

    public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository repo) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
        if (req != null) {
            httpSession.setAttribute("tenantId", TenantHolder.getCurrentTenantId());
            req = OAuth2AuthorizationRequest.from(req)
                    .build();
        }
        return req;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return defaultResolver.resolve(request, clientRegistrationId);
    }

}
