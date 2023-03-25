package com.ravening.omdbApplication.controllers;


import com.ravening.omdbApplication.config.mappers.MovieMapper;
import com.ravening.omdbApplication.exceptions.MovieNotFoundException;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;
import com.ravening.omdbApplication.services.MovieCacheService;
import com.ravening.omdbApplication.services.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    private final MovieService movieService;

    private final MovieMapper movieMapper;

    private final MovieCacheService movieCacheService;

    @Operation(summary = "Get movie based on title with Best Picture won or not")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie retrieved",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Movie.class))})
    })
    @GetMapping("/bestmovie")
    public ResponseEntity<MovieDto> didMovieWin(@Valid @RequestParam("title") String title,
                                                @RequestParam("apikey") String apikey) {
        log.info("Checking if movie {} won best picture", title);

        Optional<Movie> movie = this.movieCacheService.getMovie(title, apikey);

        return movie.map(response -> ResponseEntity.ok().body(movieMapper.toDto(response)))
                .orElseThrow(() ->
                        new MovieNotFoundException(title));

    }

    @Operation(summary = "Get top rated movies with sorting based on ratings and box office value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top rated Movies retrieved",
                    content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Movie.class))})
    })
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
