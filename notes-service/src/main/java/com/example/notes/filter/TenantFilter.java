package com.example.notes.filter;

import com.example.notes.repository.ResourceRepository;
import com.example.notes.resource.Tenant;
import com.example.notes.service.TenantService;
import com.example.notes.tenant.TenantHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TenantFilter implements Filter {

    public static final String TENANT_ID_PARAM_HEADER = "X-Tenant-Id";
    public static final String TENANT_ID_PARAM = "tenantId";
    @Autowired
    private TenantService tenantService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String tenantId = request.getHeader(TENANT_ID_PARAM_HEADER) != null ? request.getHeader(TENANT_ID_PARAM_HEADER) :
                request.getParameter(TENANT_ID_PARAM);
        if (StringUtils.isNotBlank(tenantId)) {
            Tenant tenantExample = new Tenant();
            tenantExample.setTenantId(tenantId);
            tenantService.find(String.format("{tenantId: '%s'}", tenantId))
                    .stream().findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid tenant"));
            TenantHolder.setCurrentTenantId(tenantId);
            filterChain.doFilter(request, response);
            TenantHolder.clear();
        } else {
            filterChain.doFilter(request, response);
        }
    }

}
