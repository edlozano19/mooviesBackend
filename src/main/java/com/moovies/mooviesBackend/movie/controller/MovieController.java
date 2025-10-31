package com.moovies.mooviesBackend.movie.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moovies.mooviesBackend.auth.service.UserPrincipal;
import com.moovies.mooviesBackend.movie.dto.request.AddToWatchListRequest;
import com.moovies.mooviesBackend.movie.dto.request.AddToWatchedListRequest;
import com.moovies.mooviesBackend.movie.dto.request.RateMovieRequest;
import com.moovies.mooviesBackend.movie.dto.response.MovieDetailsResponse;
import com.moovies.mooviesBackend.movie.dto.response.UserRatingResponse;
import com.moovies.mooviesBackend.movie.dto.response.WatchListItemResponse;
import com.moovies.mooviesBackend.movie.dto.response.WatchedListItemResponse;
import com.moovies.mooviesBackend.movie.dto.tmdb.TMDBSearchResponseDTO;
import com.moovies.mooviesBackend.movie.service.MovieService;
import com.moovies.mooviesBackend.movie.service.RatingService;
import com.moovies.mooviesBackend.movie.service.WatchListService;
import com.moovies.mooviesBackend.movie.service.WatchedListService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/movies")
@Slf4j
public class MovieController {
    private final MovieService movieService;
    private final WatchedListService watchedListService;
    private final WatchListService watchListService;
    private final RatingService ratingService;

    public MovieController (
            MovieService movieService,
            WatchedListService watchedListService,
            WatchListService watchListService,
            RatingService ratingService) {
        this.movieService = movieService;
        this.watchedListService = watchedListService;
        this.watchListService = watchListService;
        this.ratingService = ratingService;
    }

    @GetMapping("/search")
    public ResponseEntity<TMDBSearchResponseDTO> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page) {
        log.info("Searching movies: query={}, page={}", query, page);

        TMDBSearchResponseDTO response = movieService.searchMovies(query, page);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/tmdb/{tmdbId}")
    public ResponseEntity<MovieDetailsResponse> getMovieByTmdbId (
            @PathVariable Long tmdbId,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Getting movie details: tmdbId={}, userId={}", tmdbId, principal.getUser().getId());

        var movie = movieService.getOrCacheMovie(tmdbId);
        var userRating = ratingService.getUserRating(principal.getUser().getId(), movie.getId()).orElse(null);
        boolean onWatchList = watchListService.isOnWatchList(principal.getUser().getId(), movie.getId());
        boolean onWatchedList = watchedListService.isOnWatchedList(principal.getUser().getId(), movie.getId());

        MovieDetailsResponse response = MovieDetailsResponse.builder()
            .id(movie.getId())
            .tmdbId(movie.getTmdbId())
            .title(movie.getTitle())
            .overview(movie.getOverview())
            .posterPath(movie.getPosterPath())
            .backdropPath(movie.getBackdropPath())
            .releaseDate(movie.getReleaseDate())
            .runtime(movie.getRuntime())
            .genres(movie.getGenres())
            .averageRating(movie.getAverageRating())
            .voteCount(movie.getVoteCount())
            .userRating(userRating)
            .onWatchList(onWatchList)
            .onWatchedList(onWatchedList)
            .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/rate")
    public ResponseEntity<UserRatingResponse> rateMovie (
            @Valid @RequestBody RateMovieRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("User {} rating movie", principal.getUser().getId());

        UserRatingResponse response = ratingService.rateMovie(principal.getUser().getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{movieId}/rating")
    public ResponseEntity<Void> deleteRating (
            @PathVariable Long movieId,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("User {} deleting rating for movie {}", principal.getUser().getId(), movieId);

        ratingService.deleteRating(principal.getUser().getId(), movieId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/watchlist")
    public ResponseEntity<List<WatchListItemResponse>> getWatchList (
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Getting watch list for user {}", principal.getUser().getId());
        
        List<WatchListItemResponse> response = watchListService.getUserWatchList(principal.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/watchlist")
    public ResponseEntity<WatchListItemResponse> addToWatchList (
            @Valid @RequestBody AddToWatchListRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("User {} adding movie to watch list", principal.getUser().getId());

        WatchListItemResponse response = watchListService.addToWatchList(principal.getUser().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/watchlist/{movieId}")
    public ResponseEntity<Void> removeFromWatchList (
            @PathVariable Long movieId,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("User {} removing movie from watch list", principal.getUser().getId(), movieId);

        watchListService.removeFromWatchList(principal.getUser().getId(), movieId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/watched")
    public ResponseEntity<List<WatchedListItemResponse>> getWatchedList (
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Getting watched list for user {}", principal.getUser().getId());

        List<WatchedListItemResponse> response = watchedListService.getUserWatchedList(principal.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/watched")
    public ResponseEntity<WatchedListItemResponse> addToWatchedList (
            @Valid @RequestBody AddToWatchedListRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("User {} adding movie to watched list", principal.getUser().getId());

        WatchedListItemResponse response = watchedListService.addToWatchedList(principal.getUser().getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/watched/{movieId}")
    public ResponseEntity<Void> removeFromWatchedList (
            @PathVariable Long movieId,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("User {} removing movie from watched list", principal.getUser().getId(), movieId);
        
        watchedListService.removeFromWatchedList(principal.getUser().getId(), movieId);
        return ResponseEntity.noContent().build();
    }
}
