package com.moovies.mooviesBackend.movie.dto.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TMDBSearchResponseDTO {
    @JsonProperty("page")
    private Integer page;

    @JsonProperty("results")
    private List<TMDBMovieDTO> results;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonProperty("total_results")
    private Integer totalResults;
}
