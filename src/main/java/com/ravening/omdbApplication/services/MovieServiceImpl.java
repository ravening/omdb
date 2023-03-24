package com.ravening.omdbApplication.services;

import com.ravening.omdbApplication.config.mappers.MovieMapper;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;
import com.ravening.omdbApplication.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ravening.omdbApplication.utils.Constants.BOXOFFICEVALUE_COLUMN;
import static com.ravening.omdbApplication.utils.Constants.RATINGS_COLUMN;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    private final MovieMapper movieMapper;

    @Override
    public Optional<Movie> getMovieByTitle(String title) {
        return this.movieRepository.findMovieByNomineeEqualsIgnoreCase(title);
    }

    @Override
    public List<MovieDto> getAllMovies() {
        return this.movieRepository.findAll()
                .stream().map(movieMapper::toDto).toList();
    }

    /**
     * Function to save Movie object in db and also it evicts the entry
     * from cache so that users will get latest data from db rather than
     * stale data from cache
     *
     * @param movie
     * @return
     */
    @Override
    @CacheEvict(value = "movies", key = "#p0.nominee")
    public MovieDto saveMovie(Movie movie) {
        return movieMapper.toDto(this.movieRepository.save(movie));
    }

    /**
     * Returns top K rated movies first ordered by rating and then
     * ordered by box office value. By default, it returns top 10 movies
     * in descending order
     *
     * @param count default 10
     * @param order default descending
     * @return list of movies according to order specified
     */
    @Override
    public List<MovieDto> getTopKMovies(int count, String order) {
        Page<Movie> moviePage =
                this.movieRepository.findAll(createPageRequest(count, order));

        return moviePage.get().map(movieMapper::toDto).toList();
    }


    /**
     * Creates paging and sorting request for fetching movies.
     * By default the fetches 10 movies in descending order of ratings
     * and in case of ties then descending of box office value.
     *
     * Users can also specify "asc" for ascending order
     *
     * @param count default 10
     * @param order default desc
     * @return
     */
    private PageRequest createPageRequest(int count, String order) {
        PageRequest pageRequest;

        if ("DESC".equalsIgnoreCase(order)) {
            pageRequest = PageRequest.of(0, count, Sort.by(RATINGS_COLUMN).descending()
                    .and(Sort.by(BOXOFFICEVALUE_COLUMN).descending()));
        } else {
            pageRequest = PageRequest.of(0, count, Sort.by(RATINGS_COLUMN).ascending()
                    .and(Sort.by(BOXOFFICEVALUE_COLUMN).ascending()));
        }

        return pageRequest;
    }
}
