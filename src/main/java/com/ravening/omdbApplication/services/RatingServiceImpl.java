package com.ravening.omdbApplication.services;

import com.ravening.omdbApplication.client.OMDBClient;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private Lock lock = new ReentrantLock();
    private final MovieService movieService;

    private final MovieCacheService movieCacheService;

    private final OMDBClient omdbClient;

    /**
     * Function to add rating to a movie provided that the movie exists either in
     * local db or in OMDB.
     *
     * If the movie already exists in local db then it just updates the rating for it
     * else it will fetch the movie details from OMDB and then calculates the rating
     *
     * The logic to calculate rating is implemented in {@link RatingServiceImpl.calculateRating}
     *
     * @param title
     * @param rating
     * @param apiKey
     * @return movie with rating updated
     */
    @Override
    @Transactional
    public MovieDto addRatingToMovie(String title, double rating, String apiKey) {
        Optional<Movie> movieToRateOptional = this.movieService.getMovieByTitle(title);
        Movie movie;

        // if movie exists in db and if it doesn't have any info related to ratings
        // then fetch it from OMDB
        if (movieToRateOptional.isPresent()) {
            movie = movieToRateOptional.get();
            if (movie.getVotes() == 0 || movie.getRatings() == 0) {
                log.info("Movie {} exists in db without OMDB info. Fetching it now", title);
                Movie omdbMovie = this.omdbClient.getMovie(title, apiKey);
                movie.setVotes(omdbMovie.getVotes());
                movie.setRatings(omdbMovie.getRatings());
                movie.setBoxOfficeValue(omdbMovie.getBoxOfficeValue());
            }
        } else {
            log.info("Movie {} doesnt exist in db. Fetching it from OMDB", title);
            movie = this.omdbClient.getMovie(title, apiKey);
        }

        try {
            lock.lock();
            // calculate the rating for it inside the lock so that its thread safe
            calculateRating(movie, rating);

            log.info("New rating for movie {} is {}", movie.getNominee(), movie.getRatings());
            // update the data in cache
            this.movieCacheService.addMovie(movie);
        } finally {
            lock.unlock();
        }

        return this.movieService.saveMovie(movie);
    }

    /**
     * Function to calculate the rating for the movie. Ratings for movie is calculated as
     *
     * ratings = (total ratings / total number of votes)
     *
     * total ratings = ratings * total votes
     *
     * So when a user wants to add rating, we calculate
     *
     * total ratings = total ratings + current user rating
     * total votes = total votes + 1
     *
     * new rating = total ratings / total votes
     *
     * @param movie
     * @param rating
     */
    private void calculateRating(Movie movie, double rating) {
        log.info("Calculating the new rating for the movie {}", movie.getNominee());
        double currentRating = movie.getRatings();
        double currentTotalVotes = movie.getVotes();

        double currentTotalRating = currentRating * currentTotalVotes;

        double newTotalRating = currentTotalRating + rating;
        double newRating = newTotalRating / (currentTotalVotes + 1);

        movie.setRatings(Math.round(newRating * 100.0) / 100.0);
        movie.setVotes(movie.getVotes() + 1);
    }
}
