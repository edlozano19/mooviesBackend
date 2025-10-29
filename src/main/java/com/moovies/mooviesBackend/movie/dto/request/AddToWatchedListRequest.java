package com.moovies.mooviesBackend.movie.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToWatchedListRequest {
    @NotNull(message = "TMDB ID is required")
    private Long tmdbId;
}
