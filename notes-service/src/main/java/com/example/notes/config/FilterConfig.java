package com.example.notes.config;

import com.example.notes.filter.CorsFilter;
import com.example.notes.filter.TenantFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Autowired
    private CorsFilter corsFilter;

    @Autowired
    private TenantFilter tenantFilter;

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration(){
        FilterRegistrationBean<CorsFilter> registrationBean
                = new FilterRegistrationBean<>();
        registrationBean.setFilter(corsFilter);
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TenantFilter> tenantFilterRegistration(){
        FilterRegistrationBean<TenantFilter> registrationBean
                = new FilterRegistrationBean<>();
        registrationBean.setFilter(tenantFilter);
        registrationBean.addUrlPatterns("*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
