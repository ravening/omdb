package com.ravening.omdbApplication;

import com.ravening.omdbApplication.exceptions.MovieNotFoundException;
import com.ravening.omdbApplication.repository.MovieRepository;
import com.ravening.omdbApplication.services.MovieService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("postgres")
class OmdbApplicationTests extends AbstractIntegrationTest {

	@Autowired
	MovieRepository movieRepository;

	@Autowired
	MovieService movieService;

	@Autowired
	MockMvc mockMvc;

	private final String baseUrl = "/api/bestmovie";

	private final String apiKey = "apikey=fc3d4a73";

	@Test
	void contextLoads() {
		Assertions.assertThat(mockMvc).isNotNull();
		Assertions.assertThat(POSTGRES_SQL_CONTAINER.isRunning()).isTrue();
	}


	@Test
	@DisplayName("Throw exception when invalid key is passed")
	public void shouldThrowException_whenInvalidApiKeyIsPassed() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(baseUrl +"?title=rak&apikey=sdfsdf"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	@DisplayName("Should return valid movie")
	public void shouldReturnOk_whenSearchForValidMovie() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(baseUrl +"?title=Scream&apikey=fc3d4a73"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.nominee", is("Scream")))
				.andExpect(jsonPath("$.won", is(false)))
		;
	}

	@Test
	@DisplayName("Should throw exception when movie is not found")
	public void shouldThrowException_whenMovieNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get(baseUrl +"?title=abcdefghijk&apikey=fc3d4a73"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isNotFound())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof MovieNotFoundException))
		;
	}

	@Test
	@DisplayName("Should return top 10 rated movies")
	@Disabled("This test is flaky as the final outcomes is dependant on what OMDB returns for ratings")
	public void shouldReturnTop10Movies_whenRatingsAreAvailable() throws Exception {
		performGetRequestForMovie("Scream");

		performGetRequestForMovie("Titanic");

		performGetRequestForMovie("Chicago");

		performGetRequestForMovie("The Hurt Locker");

		performGetRequestForMovie("Slumdog Millionaire");

		performGetRequestForMovie("Inception");

		performGetRequestForMovie("True Grit");

		performGetRequestForMovie("Avatar");

		performGetRequestForMovie("Juno");

		performGetRequestForMovie("No Country for Old Men");

		mockMvc.perform(MockMvcRequestBuilders.get( "/api/topmovies"))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$", hasSize(10)))
				.andExpect(jsonPath("$.[0].nominee", is("Inception")))
				.andExpect(jsonPath("$.[1].nominee", is("No Country for Old Men")))
				.andExpect(jsonPath("$.[2].nominee", is("Slumdog Millionaire")))
				.andExpect(jsonPath("$.[3].nominee", is("Avatar")))
				.andExpect(jsonPath("$.[4].nominee", is("Titanic")))
				.andExpect(jsonPath("$.[5].nominee", is("True Grit")))
				.andExpect(jsonPath("$.[6].nominee", is("Juno")))
				.andExpect(jsonPath("$.[7].nominee", is("The Hurt Locker")))
				.andExpect(jsonPath("$.[8].nominee", is("Scream")))
				.andExpect(jsonPath("$.[9].nominee", is("Chicago")))
		;
	}

	private void performGetRequestForMovie(String title) throws Exception {
		String url = new StringBuilder()
				.append(baseUrl)
				.append("?title=")
				.append(title)
				.append("&")
				.append(apiKey)
				.toString()
				;
		mockMvc.perform(MockMvcRequestBuilders.get(url))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(jsonPath("$.nominee", is(title)))
		;
	}
}
