package com.ravening.omdbApplication;

import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;
import com.ravening.omdbApplication.models.dtos.OMDBDto;

public class MovieUtil {
    public static Movie getMovie(String title) {
        return Movie.builder()
                .nominee(title)
                .won(false).build();
    }

    public static MovieDto getMovieDto(String title) {
        return MovieDto.builder()
                .nominee(title)
                .won(false).build();
    }

    public static OMDBDto getOmdbMovieDto(String title) {
        return OMDBDto.builder()
                .title(title)
                .year("2023")
                .boxOffice("12345.67")
                .description("just scream")
                .imdbRating("7.7")
                .imdbVotes("99").build();
    }

    public static Movie getOmdbMovie(String title) {
        return Movie.builder()
                .nominee(title)
                .year("2023")
                .boxOfficeValue(12345.67)
                .description("just scream")
                .ratings(7.7)
                .votes(99).build();
    }
}
