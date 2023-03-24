package com.ravening.omdbApplication.controllers;

import com.ravening.omdbApplication.models.dtos.MovieDto;
import com.ravening.omdbApplication.models.dtos.RatingDto;
import com.ravening.omdbApplication.services.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class RatingsController {

    private final RatingService ratingService;

    @PostMapping("/rating")
    public ResponseEntity<RatingDto> addRatingToMovie(@Valid @RequestBody RatingDto ratingDto,
                                                      @RequestParam(value = "apikey") String apikey) {
        log.info("Adding rating {} to movie {}", ratingDto.getRating(), ratingDto.getTitle());
        MovieDto movie = this.ratingService.addRatingToMovie(ratingDto.getTitle(), ratingDto.getRating(), apikey);

        return ResponseEntity.ok().body(RatingDto.builder()
                .title(movie.getNominee())
                .rating(movie.getRatings()).build());
    }
}
