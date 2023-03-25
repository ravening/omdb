package com.ravening.omdbApplication.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

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
        return NumberUtils.toDouble(boxOffice.replaceAll("[$,]", ""), 0.0);
    }

    public double getVotesAsNumber() {
        return NumberUtils.toDouble(imdbVotes.replaceAll(",", ""), 0.0);
    }

    public double getRatingAsNumber() {
        return NumberUtils.toDouble(imdbRating, 0.0);
    }
}
