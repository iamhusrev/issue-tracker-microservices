package com.iamhusrev.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String userIdHeader = request.getHeader(SecurityConstants.HEADER_USER_ID);
            String userNameHeader = request.getHeader(SecurityConstants.HEADER_USER_NAME);
            String orgIdHeader = request.getHeader(SecurityConstants.HEADER_ORGANIZATION_ID);
            String roleHeader = request.getHeader(SecurityConstants.HEADER_USER_ROLE);

            if (userIdHeader != null && orgIdHeader != null) {
                Long userId = Long.parseLong(userIdHeader);
                Long organizationId = Long.parseLong(orgIdHeader);
                String role = roleHeader != null ? roleHeader : "Employee";

                TenantContext.set(new TenantContext.TenantInfo(userId, userNameHeader, organizationId, role));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userNameHeader, null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
            SecurityContextHolder.clearContext();
        }
    }
}
