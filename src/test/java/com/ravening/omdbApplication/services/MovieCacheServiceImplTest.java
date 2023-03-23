package com.ravening.omdbApplication.services;

import com.ravening.omdbApplication.MovieUtil;
import com.ravening.omdbApplication.client.OMDBClient;
import com.ravening.omdbApplication.config.mappers.MovieMapper;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.repository.MovieRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MovieCacheServiceImplTest {

    @Mock
    MovieRepository movieRepository;

    @Mock
    MovieMapper movieMapper;

    @Mock
    OMDBClient omdbClient;

    @InjectMocks
    MovieCacheServiceImpl movieCacheService;

    @Test
    @DisplayName("Should retrieve movie from cache if it exists")
    public void getMovie() {
        Movie movie = MovieUtil.getMovie("scream");
        movie.setWon(true);

        Mockito.when(movieRepository
                        .findMovieByCategoryAndNomineeEqualsIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.of(movie));

        Optional<Movie> optionalMovie = movieCacheService.getMovie("scream", "abcdef");

        assertTrue(optionalMovie.isPresent());
        assertEquals(optionalMovie.get().getNominee(), "scream");
    }

    @Test
    @DisplayName("Should retrieve movie from OMDB if it does not exist in db/cache")
    public void getMovieFromOmdb() {
        Movie movie = MovieUtil.getMovie("scream");
        movie.setWon(true);

        Mockito.when(movieRepository
                        .findMovieByCategoryAndNomineeEqualsIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(omdbClient.getMovie(anyString(), anyString()))
                .thenReturn(movie);

        Optional<Movie> optionalMovie = movieCacheService.getMovie("scream", "abcdef");

        assertTrue(optionalMovie.isPresent());
        assertEquals(optionalMovie.get().getNominee(), "scream");
    }
}
