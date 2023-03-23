package com.ravening.omdbApplication.config.mappers;

import com.ravening.omdbApplication.models.Movie;
import com.ravening.omdbApplication.models.dtos.MovieDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collection;

@Mapper(
        config = MapperConfig.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MovieMapper extends EntityMapper<MovieDto, Movie> {

    @Override
    @Mapping(target = "id", ignore = true)
    Movie toEntity(MovieDto dto);

    @Override
    @InheritInverseConfiguration(name = "toEntity")
    MovieDto toDto(Movie entity);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    @Override
    Collection<MovieDto> toDto(Collection<Movie> entityList);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    @Override
    Collection<Movie> toEntity(Collection<MovieDto> dtoList);
}
