package com.ravening.omdbApplication.repository;

import com.ravening.omdbApplication.models.Movie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static com.ravening.omdbApplication.MovieUtil.getMovie;
import static com.ravening.omdbApplication.utils.Constants.BEST_PICTURE;
import static com.ravening.omdbApplication.utils.Constants.BOXOFFICEVALUE_COLUMN;
import static com.ravening.omdbApplication.utils.Constants.RATINGS_COLUMN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("postgres")
class MovieRepositoryTest extends Databasetest {

    @Autowired
    MovieRepository movieRepository;

    @Test
    @DisplayName("Database should return the persisted entity")
    public void shouldReturnMovie_whenPersistedToDb() {
        Movie movie = Movie.builder()
                .nominee("scream")
                .won(true)
                .ratings(7.7).build();

        movieRepository.save(movie);

        Optional<Movie> movieOptional = movieRepository.findMovieByNomineeEqualsIgnoreCase("scream");
        assertTrue(movieOptional.isPresent());
        assertEquals(movieOptional.get().getNominee(), "scream");
        assertTrue(movieOptional.get().isWon());
    }

    @Test
    @DisplayName("Db should not return movie when queried for different category")
    public void shouldNotReturnMovie_whenCategoryDiffers() {
        Movie movie = getMovie("scream");
        movie.setCategory("Music");

        movieRepository.save(movie);

        Optional<Movie> movieOptional = movieRepository.findMovieByCategoryAndNomineeEqualsIgnoreCase(BEST_PICTURE, "scream");

        assertTrue(movieOptional.isEmpty());

        movieOptional = movieRepository.findMovieByCategoryAndNomineeEqualsIgnoreCase("Music", "scream");
        assertTrue(movieOptional.isPresent());
        assertEquals(movieOptional.get().getNominee(), "scream");
    }

    @Test
    @DisplayName("Db should return only the movie which has won best picture award")
    public void shouldReturnMovie_whichHasWonAward() {
        Movie movie = getMovie("Titanic");
        movie.setCategory(BEST_PICTURE);
        movie.setWon(true);

        Movie second = getMovie("scream");
        second.setWon(false);
        second.setCategory(BEST_PICTURE);

        movieRepository.save(movie);
        movieRepository.save(second);

        List<Movie> movies = movieRepository.findMovieByCategoryAndWon(BEST_PICTURE, true);

        assertFalse(movies.isEmpty());
        assertEquals(movies.size(), 1);
        assertEquals(movies.get(0).getNominee(), "Titanic");
    }

    @Test
    @DisplayName("Db should return top 5 rated movies in proper order")
    public void shouldReturnMovies_whenSortingOrderMatches() {
        Movie first = getMovie("Titanic");
        first.setRatings(9.0);

        Movie second = getMovie("scream");
        second.setRatings(8.5);

        Movie third = getMovie("inception");
        third.setRatings(8.0);

        Movie fourth = getMovie("juno");
        fourth.setRatings(7.5);

        Movie fifth = getMovie("chicago");
        fifth.setRatings(7.0);

        List<Movie> movies = List.of(first, second, third, fourth, fifth);
        movieRepository.saveAll(movies);

        Pageable descending = PageRequest.of(0, 5, Sort.by(RATINGS_COLUMN).descending()
                .and(Sort.by(BOXOFFICEVALUE_COLUMN).descending()));
        Page<Movie> moviePage = movieRepository.findAll(descending);

        movies = moviePage.stream().toList();

        assertFalse(movies.isEmpty());
        assertEquals(movies.size(), 5);
        assertEquals(movies.get(0).getNominee(), "Titanic");
        assertEquals(movies.get(1).getNominee(), "scream");
        assertEquals(movies.get(2).getNominee(), "inception");
        assertEquals(movies.get(3).getNominee(), "juno");
        assertEquals(movies.get(4).getNominee(), "chicago");

        Pageable ascending = PageRequest.of(0, 5, Sort.by(RATINGS_COLUMN).ascending()
                .and(Sort.by(BOXOFFICEVALUE_COLUMN).ascending()));
        moviePage = movieRepository.findAll(ascending);
        movies = moviePage.stream().toList();

        assertFalse(movies.isEmpty());
        assertEquals(movies.size(), 5);
        assertEquals(movies.get(4).getNominee(), "Titanic");
        assertEquals(movies.get(3).getNominee(), "scream");
        assertEquals(movies.get(2).getNominee(), "inception");
        assertEquals(movies.get(1).getNominee(), "juno");
        assertEquals(movies.get(0).getNominee(), "chicago");
    }
}
