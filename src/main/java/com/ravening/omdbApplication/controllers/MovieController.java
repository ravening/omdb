package com.ravening.omdbApplication.controllers;


import com.ravening.omdbApplication.config.mappers.MovieMapper;
import com.ravening.omdbApplication.exceptions.MovieNotFoundException;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;
import com.ravening.omdbApplication.services.MovieCacheService;
import com.ravening.omdbApplication.services.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    private final MovieService movieService;

    private final MovieMapper movieMapper;

    private final MovieCacheService movieCacheService;

    @GetMapping("/bestmovie")
    public ResponseEntity<MovieDto> didMovieWin(@Valid @RequestParam("title") String title,
                                                @RequestParam("apikey") String apikey) {
        log.info("Checking if movie {} won best picture", title);

        Optional<Movie> movie = this.movieCacheService.getMovie(title, apikey);

        return movie.map(response -> ResponseEntity.ok().body(movieMapper.toDto(response)))
                .orElseThrow(() ->
                        new MovieNotFoundException(title));

    }

    @GetMapping("/topmovies")
    public ResponseEntity<List<MovieDto>> getTopMovies(
            @RequestParam(value = "count", required = false, defaultValue = "10") int count,
            @RequestParam(value = "order", required = false, defaultValue = "desc") String order
    ) {
        log.info("Fetching top {} rated movies in {} order", count, order);
        return ResponseEntity.ok()
                .body(this.movieService.getTopKMovies(count, order));
    }
}
