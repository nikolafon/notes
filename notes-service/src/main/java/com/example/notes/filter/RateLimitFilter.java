package com.example.notes.filter;

import com.example.notes.tenant.TenantHolder;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.lettuce.core.RedisClient;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Rate limit filter.
 */
@Component
public class RateLimitFilter implements Filter {

    @Autowired
    private ProxyManager<byte[]> proxyManager;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    Supplier<BucketConfiguration> bucketConfiguration;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
            IOException, ServletException {
        if (StringUtils.isNotBlank(TenantHolder.getCurrentTenantId()) && SecurityContextHolder.getContext().getAuthentication() != null) {
            String key = TenantHolder.getCurrentTenantId() + ":" + SecurityContextHolder.getContext().getAuthentication().getName();
            Bucket bucket = proxyManager.builder().build(key.getBytes(), bucketConfiguration);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            if (probe.isConsumed()) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.setContentType("text/plain");
                httpResponse.setHeader("X-Rate-Limit-Retry-After-Seconds", "" + TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()));
                httpResponse.setStatus(HttpStatus.SC_TOO_MANY_REQUESTS);
                httpResponse.getWriter().append("Too many requests");
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }

}
