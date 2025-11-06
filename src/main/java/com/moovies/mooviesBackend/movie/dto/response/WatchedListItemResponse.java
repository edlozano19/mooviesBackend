package com.moovies.mooviesBackend.movie.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchedListItemResponse {
    private Long id;
    private MovieSummaryResponse movie;
    private LocalDateTime addedAt;
    private UserRatingResponse userRating;
}
