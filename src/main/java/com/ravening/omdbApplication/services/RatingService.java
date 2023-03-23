package com.ravening.omdbApplication.services;

import com.ravening.omdbApplication.models.dtos.MovieDto;

public interface RatingService {

    MovieDto addRatingToMovie(String title, double rating, String apiKey);
}
