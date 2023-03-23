package com.ravening.omdbApplication.config;

import com.ravening.omdbApplication.models.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

@Slf4j
public class CustomMovieKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        String key;
            if (params[0] instanceof Movie) {
                Movie movie = (Movie) params[0];
                key = movie.getNominee().toLowerCase();
            } else {
                key = params[0].toString().toLowerCase();
            }

        return String.format("%s", key);
    }
}
