package com.eventty.eventtynextgen.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableJpaRepositories( basePackages = {
    "com.eventty.eventtynextgen.auth.repository",
    "com.eventty.eventtynextgen.user.repository"
})
@EnableRedisRepositories(basePackages = {
    "com.eventty.eventtynextgen.auth.redis"
})
public class RepositoryScanConfig {

}
