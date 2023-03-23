package com.ravening.omdbApplication.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class OMDBDto {

    @JsonProperty("Title")
    private String title;
    @JsonProperty("Year")
    private String year;
    @JsonProperty("BoxOffice")
    private String boxOffice;

    @JsonProperty("Plot")
    private String description;
    @JsonProperty("imdbRating")
    private String imdbRating;
    @JsonProperty("imdbVotes")
    private String imdbVotes;

    public double getBoxOfficeValueAsNumber() {
        return Double.parseDouble(boxOffice.replaceAll("[$,]", ""));
    }

    public double getVotesAsNumber() {
        return Double.parseDouble(imdbVotes.replaceAll(",", ""));
    }
}
