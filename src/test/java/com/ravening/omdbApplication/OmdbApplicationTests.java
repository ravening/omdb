package com.ravening.omdbApplication;

import com.ravening.omdbApplication.repository.MovieRepository;
import com.ravening.omdbApplication.services.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OmdbApplicationTests extends AbstractIntegrationTest {

	@Autowired
	MovieRepository movieRepository;

	@Autowired
	MovieService movieService;

	@Autowired
	MockMvc mockMvc;


	@Test
	void contextLoads() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/bestmovie?title=rak&apikey=sdfsdf"))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

}
