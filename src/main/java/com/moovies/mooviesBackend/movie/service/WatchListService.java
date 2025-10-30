package com.moovies.mooviesBackend.movie.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moovies.mooviesBackend.movie.dto.request.AddToWatchListRequest;
import com.moovies.mooviesBackend.movie.dto.response.MovieSummaryResponse;
import com.moovies.mooviesBackend.movie.dto.response.UserRatingResponse;
import com.moovies.mooviesBackend.movie.dto.response.WatchListItemResponse;
import com.moovies.mooviesBackend.movie.entity.Movie;
import com.moovies.mooviesBackend.movie.entity.UserMovieRating;
import com.moovies.mooviesBackend.movie.entity.UserWatchList;
import com.moovies.mooviesBackend.movie.repository.UserMovieRatingRepository;
import com.moovies.mooviesBackend.movie.repository.UserWatchListRepository;
import com.moovies.mooviesBackend.user.entity.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WatchListService {
    private final UserWatchListRepository userWatchListRepository;
    private final UserMovieRatingRepository userMovieRatingRepository;
    private final MovieService movieService;

    public WatchListService (
            UserWatchListRepository userWatchListRepository,
            UserMovieRatingRepository userMovieRatingRepository,
            MovieService movieService) {
        this.userWatchListRepository = userWatchListRepository;
        this.userMovieRatingRepository = userMovieRatingRepository;
        this.movieService = movieService;
    }

    @Transactional
    public WatchListItemResponse addToWatchList(Long userId, AddToWatchListRequest request) {
        log.info("User {} adding movie with TMDB ID {} to watch list", userId, request.getTmdbId());

        Movie movie = movieService.getOrCacheMovie(request.getTmdbId());

        if (userWatchListRepository.existsByUserIdAndMovieId(userId, movie.getId())) {
            log.warn("Movie {} already on user {} watch list", movie.getId(), userId);
            throw new RuntimeException("Movie already in watch list");
        }

        UserWatchList watchListEntry = UserWatchList.builder()
            .user(User.builder().id(userId).build())
            .movie(movie)
            .build();

        UserWatchList saved = userWatchListRepository.save(watchListEntry);
        log.info("Movie added to watch list: ID {}", saved.getId());

        return convertToDTO(saved, userId);
    }

    public List<WatchListItemResponse> getUserWatchList(Long userId) {
        log.info("Getting watch list for user {}", userId);

        List<UserWatchList> watchList = userWatchListRepository.findByUserId(userId);

        return watchList.stream()
            .map(entry -> convertToDTO(entry, userId))
            .collect(Collectors.toList());
    }

    @Transactional
    public void removeFromWatchList(Long userId, Long movieId) {
        log.info("Removing movie with ID {} from watch list for user {}", movieId, userId);

        UserWatchList watchListEntry = userWatchListRepository.findByUserIdAndMovieId(userId, movieId)
            .orElseThrow(() -> new RuntimeException("Movie not found in watch list"));

        userWatchListRepository.delete(watchListEntry);
        log.info("Movie removed from watch list");
    }

    public boolean isOnWatchList(Long userId, Long movieId) {
        return userWatchListRepository.existsByUserIdAndMovieId(userId, movieId);
    }

    private WatchListItemResponse convertToDTO(UserWatchList entry, Long userId) {
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

        return WatchListItemResponse.builder()
            .id(entry.getId())
            .movie(movieSummary)
            .addedAt(entry.getAddedAt())
            .userRating(ratingResponse)
            .build();
    }
}
