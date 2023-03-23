package com.ravening.omdbApplication.services;

import com.ravening.omdbApplication.MovieUtil;
import com.ravening.omdbApplication.client.OMDBClient;
import com.ravening.omdbApplication.config.mappers.MovieMapper;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static com.ravening.omdbApplication.MovieUtil.getMovie;
import static com.ravening.omdbApplication.MovieUtil.getMovieDto;
import static com.ravening.omdbApplication.MovieUtil.getOmdbMovie;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RatingServiceImplTest {

    @Mock
    MovieServiceImpl movieService;

    @Mock
    MovieCacheServiceImpl movieCacheService;

    @Mock
    OMDBClient omdbClient;

    @Mock
    MovieMapper movieMapper;

    @InjectMocks
    RatingServiceImpl ratingService;

    @Test
    @DisplayName("Add rating to movie with all data in db")
    public void addRatingToMovieWithAllData() {
        Movie movie = getMovie("scream");
        movie.setWon(true);
        movie.setRatings(6.6);
        movie.setBoxOfficeValue(1234567.89);
        movie.setVotes(1);

        MovieDto movieDto = getMovieDto("scream");
        movieDto.setRatings(5.5);

        Mockito.when(movieService.getMovieByTitle(anyString()))
                .thenReturn(Optional.of(movie));

        Mockito.when(movieCacheService.addMovie(movie))
                .thenReturn(movie);

        Mockito.when(movieService.saveMovie(movie)).thenReturn(movieDto);
        MovieDto savedMovieDto = ratingService.addRatingToMovie("scream", 4.4, "abcdef");

        assertEquals(savedMovieDto.getRatings(), 5.5);
    }

    @Test
    @DisplayName("Add rating to movie which does not have ratings data in db")
    public void addRatingToMovieWithMissingRatingsInDb() {
        Movie movie = getMovie("scream");
        movie.setWon(true);

        MovieDto movieDto = getMovieDto("scream");
        movieDto.setRatings(5.5);

        Mockito.when(movieService.getMovieByTitle(anyString()))
                .thenReturn(Optional.of(movie));

        Mockito.when(omdbClient.getMovie(anyString(), anyString()))
                .thenReturn(getOmdbMovie("scream"));
        Mockito.when(movieCacheService.addMovie(movie))
                .thenReturn(movie);

        Mockito.when(movieService.saveMovie(movie)).thenReturn(movieDto);
        MovieDto savedMovieDto = ratingService.addRatingToMovie("scream", 4.4, "abcdef");

        assertEquals(savedMovieDto.getRatings(), 5.5);
    }


    @Test
    @DisplayName("Add rating to movie which does not exist in db")
    public void addRatingToMovieMissingInDb() {
        Movie omdbMovie = getOmdbMovie("scream");
        MovieDto movieDto = getMovieDto("scream");
        movieDto.setRatings(5.5);

        Mockito.when(movieService.getMovieByTitle(anyString()))
                .thenReturn(Optional.empty());

        Mockito.when(omdbClient.getMovie(anyString(), anyString()))
                .thenReturn(omdbMovie);
        Mockito.when(movieCacheService.addMovie(omdbMovie))
                .thenReturn(omdbMovie);

        Mockito.when(movieService.saveMovie(omdbMovie)).thenReturn(movieDto);
        MovieDto savedMovieDto = ratingService.addRatingToMovie("scream", 4.4, "abcdef");

        System.out.println(savedMovieDto);
        assertEquals(savedMovieDto.getRatings(), 5.5);
    }
}
