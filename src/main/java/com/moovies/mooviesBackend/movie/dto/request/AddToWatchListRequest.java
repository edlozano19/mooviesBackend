package com.moovies.mooviesBackend.movie.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToWatchListRequest {
    @NotNull(message = "TMDB ID is required")
    private Long tmdbId;
}
