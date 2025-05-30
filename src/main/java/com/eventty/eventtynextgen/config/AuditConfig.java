package com.eventty.eventtynextgen.config;

import com.eventty.eventtynextgen.config.provider.UserContextAuditorProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> currentAuditor() {
        return new UserContextAuditorProvider();
    }
}
