package com.example.homeworkcrawler.config;

import java.util.concurrent.ForkJoinPool;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@EnableCaching
@EnableRetry
@Configuration
public class AppConfig {

    @Bean(name = "commonForkJoinPool")
    public ForkJoinPool commonForkJoinPool() {
        return new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 4);
    }
}
