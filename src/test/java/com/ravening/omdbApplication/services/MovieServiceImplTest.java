package com.ravening.omdbApplication.services;

import com.ravening.omdbApplication.config.mappers.MovieMapper;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;
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

import java.util.List;
import java.util.Optional;

import static com.ravening.omdbApplication.MovieUtil.getMovie;
import static com.ravening.omdbApplication.MovieUtil.getMovieDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MovieServiceImplTest {

    @Mock
    MovieRepository movieRepository;

    @Mock
    MovieMapper movieMapper;

    @InjectMocks
    MovieServiceImpl movieService;


    @Test
    @DisplayName("Service should return movie by its name")
    public void getMovieByName() {

        Mockito.when(movieRepository.findMovieByNomineeEqualsIgnoreCase(anyString()))
                .thenReturn(Optional.ofNullable(Movie.builder().nominee("scream").build()));

        Optional<Movie> movieOptional = movieService.getMovieByTitle("scream");

        assertTrue(movieOptional.isPresent());
        assertEquals(movieOptional.get().getNominee(), "scream");
    }


    @Test
    @DisplayName("Service should return all requested movies")
    public void getAllMovies() {
        Movie movie = getMovie("scream");
        movie.setWon(true);
        MovieDto movieDto = getMovieDto("scream");
        movieDto.setWon(true);

        Mockito.when(movieRepository.findAll())
                .thenReturn(List.of(movie));

        Mockito.when(movieMapper.toDto(movie)).thenReturn(movieDto);

        List<MovieDto> movieDtos = movieService.getAllMovies();

       assertEquals(movieDtos.size(), 1);
       assertEquals(movieDtos.get(0).getNominee(), "scream");
       assertTrue(movieDtos.get(0).isWon());
    }

    @Test
    @DisplayName("Service should save movie successfully")
    public void saveMovie() {
        Movie movie = getMovie("scream");
        movie.setWon(false);
        movie.setRatings(6.6);

        MovieDto movieDto = getMovieDto("scream");
        movieDto.setWon(false);
        movieDto.setRatings(6.6);

        Mockito.when(movieRepository.save(movie)).thenReturn(movie);
        Mockito.when(movieMapper.toDto(movie)).thenReturn(movieDto);

        MovieDto savedMovie = movieService.saveMovie(movie);

        assertNotNull(savedMovie);
        assertFalse(savedMovie.isWon());
        assertEquals(savedMovie.getNominee(), "scream");
        assertEquals(6.6, savedMovie.getRatings());
    }

    @Test
    @DisplayName("Service should return the top 3 rated movies in descending order of rating and boxoffice value")
    public void getTop3RatedMoviesOrderedByBoxOffice() {

    }
}
