package com.ravening.omdbApplication.client;

import com.ravening.omdbApplication.exceptions.MovieNotFoundException;
import com.ravening.omdbApplication.exceptions.OMDBException;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.OMDBDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.MessageFormat;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
@Slf4j
public class OMDBClient {

    @Value("${omdb.url}")
    private String omdbUrl;

    private final RestTemplate restTemplate;


    public OMDBDto retrieveOmdbData(String title, String omdbApiKey) {
        log.info(MessageFormat.format("Fetching movie data for title {0}", title));
        URI uri = createUri(title, omdbApiKey);
//        return Optional.ofNullable(
//                restTemplate.exchange(uri, HttpMethod.GET, null, OMDBDto.class)
//                        .getBody()
//        );


        try {
            return restTemplate.getForObject(uri, OMDBDto.class);
        } catch (Exception e) {
            log.error("Exception happened while fetching data from OMDB");
            log.error("{}", e.getMessage());
            throw new OMDBException(e);
        }
    }
    private URI createUri(String title, String omdbApiKey) {
        return UriComponentsBuilder
                .fromUriString(omdbUrl)
                .queryParam("apikey", omdbApiKey)
                .queryParam("t", title)
                .build().toUri();
    }

    public void checkIfMovieExistsInOmdb(OMDBDto omdbDto, String title) {
        ofNullable(omdbDto)
                .flatMap(omdb -> ofNullable(omdb.getTitle()))
                .orElseThrow(() -> new MovieNotFoundException(title));
    }
    public Movie getMovie(String title, String omdbApiKey) {
        OMDBDto omdbDto = retrieveOmdbData(title, omdbApiKey);
        checkIfMovieExistsInOmdb(omdbDto, title);

        return Movie.builder()
                .nominee(title)
                .year(omdbDto.getYear())
                .won(false)
                .category("Best Picture")
                .description(omdbDto.getDescription())
                .ratings((Double.parseDouble(omdbDto.getImdbRating())))
                .boxOfficeValue(omdbDto.getBoxOfficeValueAsNumber())
                .votes(omdbDto.getVotesAsNumber()).build();
    }
}
