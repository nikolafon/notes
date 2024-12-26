package com.example.notes.filter;

import com.example.notes.resource.Tenant;
import com.example.notes.repository.TenantRepository;
import com.example.notes.tenant.TenantHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TenantFilter implements Filter {

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String tenantId = request.getHeader("X-Tenant-Id");
        if (StringUtils.isNotBlank(tenantId)) {
            Tenant tenantExample = new Tenant();
            tenantExample.setTenantId(tenantId);
            TenantHolder.setCurrentTenantId(tenantId);
            filterChain.doFilter(request, response);
            TenantHolder.clear();
        } else {
            filterChain.doFilter(request, response);
        }
    }

}
