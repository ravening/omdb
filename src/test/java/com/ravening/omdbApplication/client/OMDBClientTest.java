package com.ravening.omdbApplication.client;

import com.ravening.omdbApplication.exceptions.MovieNotFoundException;
import com.ravening.omdbApplication.exceptions.OMDBException;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.OMDBDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OMDBClientTest {

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    OMDBClient omdbClient;

    @Test
    @DisplayName("OMDB should return when movie name is valid")
    public void shouldReturnMovie_whenMovieNameIsValid() {
        OMDBDto omdbMockDto = OMDBDto.builder()
                .title("scream")
                .imdbVotes("1000")
                .year("2020")
                .imdbRating("5.5")
                .build();
        Mockito.when(restTemplate.getForObject(any(), any()))
                .thenReturn(omdbMockDto);
        ReflectionTestUtils.setField(omdbClient, "omdbUrl", "https://www.omdbapi.com/");
        OMDBDto omdbDto = omdbClient.retrieveOmdbData("scream", "abcdef");

        assertNotNull(omdbDto);
        assertEquals("scream", omdbDto.getTitle());
        assertEquals(1000, omdbDto.getVotesAsNumber());
    }

    @Test
    @DisplayName("should throw exception when apikey is invalid")
    public void shouldThrowException_whenApikeyIsInvalid() {

        Mockito.when(restTemplate.getForObject(any(), any()))
                .thenThrow(new OMDBException(new Exception("Invalid api key")));
        ReflectionTestUtils.setField(omdbClient, "omdbUrl", "https://www.omdbapi.com/");

        OMDBException omdbException = Assertions.assertThrows(OMDBException.class,
                () -> omdbClient.retrieveOmdbData("scream", "$%^&*("));

        assertEquals("Invalid api key", omdbException.getMessage());
    }

    @Test
    @DisplayName("should throw exception when movie is not found")
    public void shouldThrowException_whenMovieNotFound() {

        Mockito.when(restTemplate.getForObject(any(), any()))
                .thenThrow(new OMDBException(new Exception("Movie not found")));
        ReflectionTestUtils.setField(omdbClient, "omdbUrl", "https://www.omdbapi.com/");

        OMDBException omdbException = Assertions.assertThrows(OMDBException.class,
                () -> omdbClient.retrieveOmdbData("", "$%^&*("));

        assertEquals("Movie not found", omdbException.getMessage());
    }

    @Test
    @DisplayName("should throw movie not found exception")
    public void shouldThrowMovieNotFoundException() {

        MovieNotFoundException movieNotFoundException = Assertions.assertThrows(MovieNotFoundException.class,
                () -> omdbClient.checkIfMovieExistsInOmdb(null, null));

        System.out.println(movieNotFoundException.getMessage());
        assertEquals("Movie with title null not found in OMDB", movieNotFoundException.getMessage());
    }

    @Test
    @DisplayName("should parse Double values properly")
    public void shouldParseDoubleValue_whenStringIsEmptyOrInvalid() {
        OMDBDto omdbDto = OMDBDto.builder()
                .title("scream")
                .year("2020")
                .description("nothing")
                .imdbRating("N/A")
                .boxOffice("")
                .imdbVotes("N/A").build();

        Mockito.when(restTemplate.getForObject(any(), any()))
                .thenReturn(omdbDto);
        ReflectionTestUtils.setField(omdbClient, "omdbUrl", "https://www.omdbapi.com/");
        MovieNotFoundException movieNotFoundException = Assertions.assertThrows(MovieNotFoundException.class,
                () -> omdbClient.checkIfMovieExistsInOmdb(null, null));

        Movie movie = omdbClient.getMovie("scream", "abcdef");

        assertNotNull(movie);
        assertEquals(0.0, movie.getVotes());
        assertEquals(0.0, movie.getRatings());
        assertEquals(0.0, movie.getBoxOfficeValue());
    }

}
