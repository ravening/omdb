package com.ravening.omdbApplication.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration extends CachingConfigurerSupport {

    @Bean("bestpictures")
    public KeyGenerator keyGenerator() {
        return new CustomMovieKeyGenerator();
    }
}
