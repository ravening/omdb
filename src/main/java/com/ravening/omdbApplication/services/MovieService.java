package com.ravening.omdbApplication.services;

import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;

import java.util.List;
import java.util.Optional;

public interface MovieService {
    Optional<Movie> getMovieByTitle(String title);

    List<MovieDto> getAllMovies();

    MovieDto saveMovie(Movie movie);

    List<MovieDto> getTopKMovies(int count, String order);
}
