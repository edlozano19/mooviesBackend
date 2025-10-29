package com.moovies.mooviesBackend.movie.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieSummaryResponse {
    private Long id;
    private Long tmdbId;
    private String title;
    private String posterPath;
    private LocalDate releaseDate;
    private BigDecimal averageRating;
    private Integer voteCount;
}
