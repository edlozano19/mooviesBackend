package com.moovies.mooviesBackend.movie.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.moovies.mooviesBackend.movie.dto.request.AddToWatchedListRequest;
import com.moovies.mooviesBackend.movie.dto.response.MovieSummaryResponse;
import com.moovies.mooviesBackend.movie.dto.response.UserRatingResponse;
import com.moovies.mooviesBackend.movie.dto.response.WatchedListItemResponse;
import com.moovies.mooviesBackend.movie.entity.Movie;
import com.moovies.mooviesBackend.movie.entity.UserMovieRating;
import com.moovies.mooviesBackend.movie.entity.UserWatchedList;
import com.moovies.mooviesBackend.movie.repository.UserMovieRatingRepository;
import com.moovies.mooviesBackend.movie.repository.UserWatchedListRepository;
import com.moovies.mooviesBackend.user.entity.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class WatchedListService {
    private final MovieService movieService;
    private final UserMovieRatingRepository userMovieRatingRepository;
    private final UserWatchedListRepository userWatchedListRepository;

    public WatchedListService(
            MovieService movieService,
            UserMovieRatingRepository userMovieRatingRepository,
            UserWatchedListRepository userWatchedListRepository) {
        this.movieService = movieService;
        this.userMovieRatingRepository = userMovieRatingRepository;
        this.userWatchedListRepository = userWatchedListRepository;
    }

    @Transactional
    public WatchedListItemResponse addToWatchedList(Long userId, AddToWatchedListRequest request) {
        log.info("User {} adding movie with TMDB ID {} to watched list", userId, request.getTmdbId());

        Movie movie = movieService.getOrCacheMovie(request.getTmdbId());

        if (userWatchedListRepository.existsByUserIdAndMovieId(userId, movie.getId())) {
            log.warn("Movie {} already on user {} watched list", movie.getId(), userId);
            throw new RuntimeException("Movie already in watched list");
        }
        
        UserWatchedList watchedListEntry = UserWatchedList.builder()
            .user(User.builder().id(userId).build())
            .movie(movie)
            .build();

        UserWatchedList saved = userWatchedListRepository.save(watchedListEntry);
        log.info("Movie added to watched list: ID {}", saved.getId());

        return convertToDTO(saved, userId);
    }

    public List<WatchedListItemResponse> getUserWatchedList(Long userId) {
        log.info("Getting watched list for user {}", userId);

        List<UserWatchedList> watchedList = userWatchedListRepository.findByUserId(userId);

        return watchedList.stream()
            .map(entry -> convertToDTO(entry, userId))
            .collect(Collectors.toList());
    }

    @Transactional
    public void removeFromWatchedList(Long userId, Long movieId) {
        log.info("Removing movie with ID {} from watched list for user {}", movieId, userId);

        UserWatchedList watchedListEntry = userWatchedListRepository.findByUserIdAndMovieId(userId, movieId)
            .orElseThrow(() -> new RuntimeException("Movie not found in watched list"));

        userWatchedListRepository.delete(watchedListEntry);
        log.info("Movie removed from watched list");
    }

    @Transactional
    public void removeFromWatchedListByTmdbId(Long userId, Long tmdbId) {
        log.info("Removing movie with TMDB ID {} from watched list for user {}", tmdbId, userId);

        Movie movie = movieService.getCachedMovieByTmdbId(tmdbId)
            .orElseThrow(() -> new RuntimeException("Movie not found"));

        UserWatchedList watchedListEntry = userWatchedListRepository.findByUserIdAndMovieId(userId, movie.getId())
            .orElseThrow(() -> new RuntimeException("Movie not found in watched list"));

        userWatchedListRepository.delete(watchedListEntry);
        log.info("Movie removed from watched list");
    }

    public boolean isOnWatchedList(Long userId, Long movieId) {
        log.info("Checking if movie with ID {} is on watched list for user {}", movieId, userId);

        return userWatchedListRepository.existsByUserIdAndMovieId(userId, movieId);
    }

    private WatchedListItemResponse convertToDTO(UserWatchedList entry, Long userId) {
        Movie movie = entry.getMovie();

        Optional<UserMovieRating> rating = userMovieRatingRepository.findByUserIdAndMovieId(userId, movie.getId());

        UserRatingResponse ratingResponse = rating.map(r -> UserRatingResponse.builder() 
            .id(r.getId())
            .rating(r.getRating())
            .createdAt(r.getCreatedAt())
            .updatedAt(r.getUpdatedAt())
            .build()).orElse(null);

        MovieSummaryResponse movieSummary = MovieSummaryResponse.builder()
            .id(movie.getId())
            .tmdbId(movie.getTmdbId())
            .title(movie.getTitle())
            .posterPath(movie.getPosterPath())
            .releaseDate(movie.getReleaseDate())
            .averageRating(movie.getAverageRating())
            .voteCount(movie.getVoteCount())
            .build();

        return WatchedListItemResponse.builder()
            .id(entry.getId())
            .movie(movieSummary)
            .addedAt(entry.getAddedAt())
            .userRating(ratingResponse)
            .build();
    }
}
