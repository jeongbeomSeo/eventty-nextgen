package com.eventty.eventtynextgen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
    "com.eventty.eventtynextgen.auth.repository",
    "com.eventty.eventtynextgen.user.repository"
})
@EnableRedisRepositories(basePackages = {
    "com.eventty.eventtynextgen.auth.redis"
})
public class EventtyNextgenApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventtyNextgenApplication.class, args);
    }

}