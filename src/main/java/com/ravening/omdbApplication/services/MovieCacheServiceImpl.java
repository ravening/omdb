package com.ravening.omdbApplication.services;

import com.ravening.omdbApplication.client.OMDBClient;
import com.ravening.omdbApplication.config.mappers.MovieMapper;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;
import com.ravening.omdbApplication.repository.MovieRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ravening.omdbApplication.utils.Constants.BEST_PICTURE;


@Service
@CacheConfig(cacheNames = "movies")
@RequiredArgsConstructor
@Slf4j
public class MovieCacheServiceImpl implements MovieCacheService {

    private final MovieRepository movieRepository;

    private final MovieMapper movieMapper;

    private final OMDBClient omdbClient;

    @Override
    @Cacheable(value = "movies",keyGenerator = "bestpictures")
    public Optional<Movie> getMovie(String nominee, String apiKey) {
        log.info("Fetching movie {} from database", nominee);

        // check if the movie exists in the database
        Optional<Movie> movieExistsInDb = this.movieRepository.findMovieByCategoryAndNomineeEqualsIgnoreCase(BEST_PICTURE, nominee);

        if (movieExistsInDb.isEmpty()) {
            log.info("Fetching movie {} from OMDB", nominee);
            Movie movie = this.omdbClient.getMovie(nominee, apiKey);
            this.movieRepository.save(movie);

            return Optional.of(movie);
        }

        return movieExistsInDb;
    }

    @Override
    public List<MovieDto> getMovies() {
        log.info("Fetching all best movies from db which won best picture award");
        return
                this.movieRepository.findMovieByCategoryAndWon(BEST_PICTURE, true)
                        .stream().map(movieMapper::toDto).toList();
    }

    @Override
    @CachePut(value = "movies",  keyGenerator = "bestpictures")
    public Movie addMovie(Movie movie) {
        return movie;
    }

    @PreDestroy
    public void clear() {
        this.movieRepository.deleteAll();
    }
}
