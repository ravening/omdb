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

    @Override
    @CacheEvict(value = "movies", key = "#p0.nominee")
    public MovieDto saveMovie(Movie movie) {
        return movieMapper.toDto(this.movieRepository.save(movie));
    }

    @Override
    public List<MovieDto> getTopKMovies(int count, String order) {
        Page<Movie> moviePage =
                this.movieRepository.findAll(createPageRequest(count, order));

        return moviePage.get().map(movieMapper::toDto).toList();
    }


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
