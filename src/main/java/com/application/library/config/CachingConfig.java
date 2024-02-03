package com.application.library.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {
    public static final String LATE_FEE_PER_DAY = "late-fee-per-day";
    public static final String LEND_DAY = "lend_day";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                LATE_FEE_PER_DAY,
                LEND_DAY
        );
    }
}
