package com.iamhusrev;

import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfoTestConfig {

    @Bean
    InfoContributor testInfoContributor() {
        return builder -> builder.withDetail("forced", "true");
    }
}
