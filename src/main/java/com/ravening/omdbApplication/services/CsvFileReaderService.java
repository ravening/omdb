package com.ravening.omdbApplication.services;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.ravening.omdbApplication.client.OMDBClient;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.OMDBDto;
import com.ravening.omdbApplication.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ravening.omdbApplication.utils.Constants.BEST_PICTURE;


@Service
@Slf4j
@RequiredArgsConstructor
public class CsvFileReaderService implements FileReaderService {

    @Value("${movies.filename}")
    String csvFileName;

    @Value("${omdb.api.key}")
    String omdbApiKey;

    private final MovieRepository movieRepository;
    private final OMDBClient omdbClient;

    /**
     * Function to read from csv file, create {@link Movie} object out of it
     * and persist in db.
     *
     * If OMDB apikey is provided in {@link application.properties#omdb.api.key} then it will
     * also fetch movie related info from OMDb and persists in db else it will skip
     * fetching data from OMDB.
     *
     * @throws IOException
     */
    @Override
    @EventListener(ApplicationReadyEvent.class)
    public void readFile() throws IOException {

        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(csvFileName);

        if (inputStream == null) {
            throw new FileNotFoundException(String.format("Unable to find %s under resources folder", csvFileName));
        }

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .build();

        List<Movie> movies = new ArrayList<>();
        try (InputStreamReader isr = new InputStreamReader(inputStream)) {
            CSVReader reader = new CSVReaderBuilder(isr)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();

            String[] line;

            while ((line = reader.readNext()) != null) {
                if (BEST_PICTURE.equalsIgnoreCase(line[1])) {
                    Movie movie = Movie.builder()
                            .year(line[0])
                            .category(line[1])
                            .nominee(line[2])
                            .description(line[3])
                            .won("YES".equalsIgnoreCase(line[4])).build();

                    // if apikey is already provided in application.properties then fetch missing details from OMDB
                    if (omdbApiKey != null && !StringUtils.trimAllWhitespace(omdbApiKey).equalsIgnoreCase("")) {
                        OMDBDto omdbDto =
                                this.omdbClient.retrieveOmdbData(movie.getNominee(), omdbApiKey);
                        this.omdbClient.checkIfMovieExistsInOmdb(omdbDto, movie.getNominee());
                        movie.setRatings(Double.parseDouble(omdbDto.getImdbRating()));
                        movie.setVotes(omdbDto.getVotesAsNumber());
                        movie.setBoxOfficeValue(omdbDto.getBoxOfficeValueAsNumber());
                    }
                    movies.add(movie);
                }
            }
        }

        this.movieRepository.saveAll(movies);

//        List<Movie> movieList = parseCsvFileToMovieBeans(csvFileName, Movie.class);

    }

    public static <T> List<T> parseCsvFileToMovieBeans(final String filename,
                                                  final Class<T> beanClass) {
        CSVReader reader = null;
        try {
            // Read the csv file from resources directory
            InputStream inputStream = CsvFileReaderService.class
                    .getClassLoader()
                    .getResourceAsStream(filename);
            InputStreamReader isr = new InputStreamReader(inputStream);

            // csv parser with , as delimiter
            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            // create csv reader and skip reading header line
            reader = new CSVReaderBuilder(isr)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();

            final HeaderColumnNameMappingStrategy<T> strategy =
                    new HeaderColumnNameMappingStrategy<T>();
            strategy.setType(beanClass);
            final CsvToBean<T> csv = new CsvToBean<T>();
            csv.setFilter(strings -> {
                for (String token : strings) {
                    if (token != null && token.length() > 0) {
                        return true;
                    }
                }

                return false;
            });

            return csv.parse(strategy, reader);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    // ignore
                }
            }
        }

        return Collections.emptyList();
    }
}
