package com.iamhusrev.config;

import com.iamhusrev.security.TenantFilter;
import com.iamhusrev.security.TenantHibernateAspect;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, EntityManager entityManager) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tenantFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(tenantHibernateAspect(entityManager), TenantFilter.class);
        return http.build();
    }

    @Bean
    public TenantFilter tenantFilter() {
        return new TenantFilter();
    }

    @Bean
    public TenantHibernateAspect tenantHibernateAspect(EntityManager entityManager) {
        return new TenantHibernateAspect(entityManager);
    }
}
