package com.ravening.omdbApplication.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SchedulerService {

    private final MovieCacheService movieCacheService;

    /**
     * Function to clear the movies cache every 5 minutes
     */
    @Scheduled(fixedRateString = "${spring.app.caching.movies.ttl}")
    public void clearMoviesCache() {
        this.movieCacheService.emptyMoviesCache();
    }
}
