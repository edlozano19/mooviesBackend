package com.moovies.mooviesBackend.movie.dto.tmdb;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TMDBGenreDTO {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;
}
