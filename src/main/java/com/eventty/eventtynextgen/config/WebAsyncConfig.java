package com.eventty.eventtynextgen.config;

import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class WebAsyncConfig implements WebMvcConfigurer {

    @Bean("gcsIoExecutor")
    public ThreadPoolTaskExecutor gcsIoExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(4);
        ex.setMaxPoolSize(4);
        ex.setQueueCapacity(100);
        ex.setRejectedExecutionHandler(new CallerRunsPolicy());
        ex.setThreadNamePrefix("gcsIoExecutor-");
        ex.setKeepAliveSeconds(30);
        ex.initialize();
        return ex;
    }
}
