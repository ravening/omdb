package com.ravening.omdbApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "OMDB API", version = "3.0", description = "OMDB rest api's"))
public class OmdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmdbApplication.class, args);
	}

}
