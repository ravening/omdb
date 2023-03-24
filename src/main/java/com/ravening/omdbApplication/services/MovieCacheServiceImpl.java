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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Function to fetch movie from based on its title. If its present in cache
     * then it will be returned immediately else it will be searched in the local
     * database.
     * If it exists in local db but missing few movie related info then it will be
     * fetched from OMDB.
     * If movie doesnt exist is local db then it will be fetched from OMDb, persisted
     * in local db and loaded in cache also.
     *
     * @param nominee
     * @param apiKey
     * @return the movie to search for
     */
    @Override
    @Cacheable(value = "movies",keyGenerator = "bestpictures")
    @Transactional
    public Optional<Movie> getMovie(String nominee, String apiKey) {
        // check if the movie exists in the database
        Optional<Movie> movieExistsInDb = this.movieRepository.findMovieByCategoryAndNomineeEqualsIgnoreCase(BEST_PICTURE, nominee);
        log.info("Fetching movie {} from database", nominee);

        Movie movie;
        if (movieExistsInDb.isEmpty()) {
            log.info("Fetching movie {} from OMDB", nominee);
            movie = this.omdbClient.getMovie(nominee, apiKey);
        } else {
            // fetch from OMDB if few details are missing
            movie = movieExistsInDb.get();
            if (movie.getVotes() == 0 || movie.getRatings() == 0 || movie.getBoxOfficeValue() == 0) {
                Movie omdbMovie = this.omdbClient.getMovie(nominee, apiKey);
                movie.setVotes(omdbMovie.getVotes());
                movie.setRatings(omdbMovie.getRatings());
                movie.setBoxOfficeValue(omdbMovie.getBoxOfficeValue());
            }
        }

        this.movieRepository.save(movie);
        return Optional.of(movie);
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

    /**
     * Clear out the cache every 5 minutes so that users
     * wont get the stale data all the time.
     */
    @Override
    @CacheEvict(cacheNames = "movies", allEntries = true)
    public void emptyMoviesCache() {
    }

    @PreDestroy
    public void clear() {
        this.movieRepository.deleteAll();
    }
}
