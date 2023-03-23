package com.ravening.omdbApplication.services;

import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;

import java.util.List;
import java.util.Optional;

public interface MovieCacheService {

    Optional<Movie> getMovie(String title, String apiKey);

//    Optional<MovieDto> getMovie(String title);
//    List<Movie> getMovies();

    List<MovieDto> getMovies();

    Movie addMovie(Movie movie);
}
