package com.moovies.mooviesBackend.movie.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RateMovieRequest {
    @NotNull(message = "TMDB ID is required")
    private Long tmdbId;

    @NotNull(message = "Rating is required")
    @DecimalMin(value = "0.25", message = "Rating must be at least 0.25")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    private BigDecimal rating;
}
