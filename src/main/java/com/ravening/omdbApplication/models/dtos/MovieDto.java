package com.ravening.omdbApplication.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {

    String year;
    String category;
    String nominee;
    String description;
    boolean won;
    double ratings;
    double votes;
    double boxOfficeValue;
}
