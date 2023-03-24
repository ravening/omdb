package com.ravening.omdbApplication.config;

import com.ravening.omdbApplication.models.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

@Slf4j
public class CustomMovieKeyGenerator implements KeyGenerator {
    /**
     * By default, keys are case sensitive in spring. So add
     * a custom key generator which makes the key case insensitive
     * so that when we search for either "Titanic" or "titanic" or
     * "TITANIC", it exists in the cache
     *
     * @param target
     * @param method
     * @param params
     * @return
     */
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
