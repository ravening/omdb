package com.ravening.omdbApplication.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ravening.omdbApplication.config.mappers.MovieMapper;
import com.ravening.omdbApplication.models.dtos.MovieDto;
import com.ravening.omdbApplication.models.dtos.RatingDto;
import com.ravening.omdbApplication.services.MovieCacheService;
import com.ravening.omdbApplication.services.MovieService;
import com.ravening.omdbApplication.services.RatingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@WebMvcTest(RatingsController.class)
@ExtendWith(SpringExtension.class)
class RatingsControllerTest {

    @MockBean
    MovieService movieService;

    @MockBean
    MovieCacheService movieCacheService;

    @MockBean
    RatingService ratingService;

    @MockBean
    MovieMapper movieMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Should throw exception when title is missing")
    public void shouldThrowException_whenMovieNameIsMissing() throws Exception {
        RatingDto ratingDto = RatingDto.builder().build();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/rating")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(ratingDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof MethodArgumentNotValidException
                ))
        ;

    }

    @Test
    @DisplayName("Should throw exception when rating is missing")
    public void shouldThrowException_whenRatingIsMissing() throws Exception {
        RatingDto ratingDto = RatingDto.builder()
                .title("Chicago").build();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof MethodArgumentNotValidException
                ))
        ;

    }


    @Test
    @DisplayName("Should throw exception when apikey is missing")
    public void shouldThrowException_whenApiKeyIsMissing() throws Exception {
        RatingDto ratingDto = RatingDto.builder()
                .title("Chicago")
                .rating(5.5).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(
                        result.getResolvedException() instanceof MissingServletRequestParameterException
                ))
        ;

    }

    @Test
    @DisplayName("Should throw exception when apikey is missing")
    public void shouldRateMove_whenValidDataIsPassed() throws Exception {
        RatingDto ratingDto = RatingDto.builder()
                .title("Chicago")
                .rating(5.5).build();
        MovieDto movieDto = MovieDto.builder()
                .nominee("Chicago").ratings(5.5).build();

        given(ratingService.addRatingToMovie(anyString(), anyDouble(), anyString()))
                .willReturn(movieDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/rating?apikey=sdfsdf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Chicago"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rating").value(5.5))

        ;

    }

}
