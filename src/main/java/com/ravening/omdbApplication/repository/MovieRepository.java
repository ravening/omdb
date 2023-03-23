package com.ravening.omdbApplication.repository;

import com.ravening.omdbApplication.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findMovieByCategoryAndNomineeEqualsIgnoreCase(String category, String nominee);

    List<Movie> findMovieByCategoryAndWon(String category, boolean won);

    Optional<Movie> findMovieByNomineeEqualsIgnoreCase(String nominee);

    Page<Movie> findAll(Pageable pageable);
}
