package com.iamhusrev.config;

import com.iamhusrev.security.SecurityConstants;
import com.iamhusrev.security.TenantContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignTenantInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        TenantContext.TenantInfo info = TenantContext.get();
        if (info != null) {
            template.header(SecurityConstants.HEADER_USER_ID, String.valueOf(info.getUserId()));
            template.header(SecurityConstants.HEADER_ORGANIZATION_ID, String.valueOf(info.getOrganizationId()));
            template.header(SecurityConstants.HEADER_USER_NAME, info.getUserName());
            template.header(SecurityConstants.HEADER_USER_ROLE, info.getRole());
        }
    }
}
