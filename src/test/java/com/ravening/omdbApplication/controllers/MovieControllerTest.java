package com.ravening.omdbApplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.exception.BadRequestException;
import com.ravening.omdbApplication.AbstractIntegrationTest;
import com.ravening.omdbApplication.MovieUtil;
import com.ravening.omdbApplication.config.mappers.MovieMapper;
import com.ravening.omdbApplication.exceptions.MovieNotFoundException;
import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;
import com.ravening.omdbApplication.services.MovieCacheService;
import com.ravening.omdbApplication.services.MovieService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static com.ravening.omdbApplication.MovieUtil.getMovie;
import static com.ravening.omdbApplication.MovieUtil.getMovieDto;
import static org.mockito.BDDMockito.given;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@WebMvcTest(MovieController.class)
@ExtendWith(SpringExtension.class)
class MovieControllerTest  {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieMapper movieMapper;

    @MockBean
    private MovieCacheService movieCacheService;

    @MockBean
    MovieService movieService;

    @Test
    @DisplayName("Should throw exception when title is missing")
    public void shouldThrowException_whenMovieNameIsMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bestmovie"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof MissingServletRequestParameterException
                ))
        ;

    }

    @Test
    @DisplayName("Should throw exception when apikey is missing")
    public void shouldThrowException_whenApiKeyIsMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bestmovie?title=dummy"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof MissingServletRequestParameterException
                ))
        ;

    }

    @Test
    @DisplayName("Should throw exception when doesnt exist in db")
    public void shouldThrowException_whenMovieNotFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/bestmovie?title=rak&apikey=sdfsdf"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof MovieNotFoundException
                ));
    }
    @Test
    @DisplayName("Should return the movie if it exists in db")
    public void shouldReturnMovie_whenExistsInDb() throws Exception {
        Movie movie = getMovie("rakesh");

        MovieDto movieDto = getMovieDto("rakesh");

        given(movieCacheService.getMovie(anyString(), anyString()))
                .willReturn(Optional.of(movie));
        given(movieMapper.toDto(movie)).willReturn(movieDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bestmovie?title=rakesh&apikey=sdfsdf"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nominee").value("rakesh"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.won").value(false))
        ;
    }

    @Test
    @DisplayName("Should indicate if movie won Best Picture award")
    public void shouldIndicate_ifMovieWonBestPictureAward() throws Exception {
        Movie movie = getMovie("Chicago");
        movie.setWon(true);
        MovieDto movieDto = getMovieDto("Chicago");
        movieDto.setWon(true);

        given(movieCacheService.getMovie(anyString(), anyString()))
                .willReturn(Optional.of(movie));
        given(movieMapper.toDto(movie)).willReturn(movieDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/bestmovie?title=chicago&apikey=sdfsdf"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nominee").value("Chicago"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.won").value(true))
        ;
    }


}
