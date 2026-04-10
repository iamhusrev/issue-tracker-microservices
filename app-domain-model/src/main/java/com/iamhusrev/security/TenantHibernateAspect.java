package com.iamhusrev.security;

import jakarta.persistence.EntityManager;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TenantHibernateAspect extends OncePerRequestFilter {

    private final EntityManager entityManager;

    public TenantHibernateAspect(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Long organizationId = TenantContext.getOrganizationId();
        if (organizationId != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("organizationFilter").setParameter("orgId", organizationId);
        }
        filterChain.doFilter(request, response);
    }
}
