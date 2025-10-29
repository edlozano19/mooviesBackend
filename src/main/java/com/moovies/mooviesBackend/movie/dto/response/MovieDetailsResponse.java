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
public class MovieDetailsResponse {
    private Long id;
    private Long tmdbId;
    private String title;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private LocalDate releaseDate;
    private Integer runtime;
    private String genres;
    private BigDecimal averageRating;
    private Integer voteCount;
    private UserRatingResponse userRating;
    private Boolean onWatchList;
    private Boolean onWatchedList;
}
