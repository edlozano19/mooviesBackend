package com.moovies.mooviesBackend.movie.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moovies.mooviesBackend.movie.dto.request.RateMovieRequest;
import com.moovies.mooviesBackend.movie.dto.response.UserRatingResponse;
import com.moovies.mooviesBackend.movie.entity.Movie;
import com.moovies.mooviesBackend.movie.entity.UserMovieRating;
import com.moovies.mooviesBackend.movie.repository.MovieRepository;
import com.moovies.mooviesBackend.movie.repository.UserMovieRatingRepository;
import com.moovies.mooviesBackend.user.entity.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RatingService {
    private final MovieService movieService;
    private final UserMovieRatingRepository userMovieRatingRepository;
    private final MovieRepository movieRepository;

    public RatingService (
            MovieService movieService,
            UserMovieRatingRepository userMovieRatingRepository,
            MovieRepository movieRepository) {
        this.movieService = movieService;
        this.userMovieRatingRepository = userMovieRatingRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional
    public UserRatingResponse rateMovie(Long userId, RateMovieRequest request) {
        log.info("User {} rating movie with TMDB ID {}: {}", userId, request.getTmdbId(), request.getRating());

        Movie movie = movieService.getOrCacheMovie(request.getTmdbId());

        Optional<UserMovieRating> existingRating = userMovieRatingRepository.findByUserIdAndMovieId(userId, movie.getId());

        UserMovieRating rating;

        if (existingRating.isPresent()) {
            rating = existingRating.get();
            BigDecimal oldRating = request.getRating();
            rating.setRating(request.getRating());
            log.info("Updating rating from {} to {}", oldRating, request.getRating());
        }
        else {
            rating = UserMovieRating.builder()
                .user(User.builder().id(userId).build())
                .movie(movie)
                .rating(request.getRating())
                .build();
            log.info("Creating new rating");
        }

        UserMovieRating savedRating = userMovieRatingRepository.save(rating);
        updateMovieAverageRating(movie.getId());
        log.info("Rating saved successfully: ID {}", savedRating.getId());

        return convertToDTO(savedRating);
    }

    public Optional<UserRatingResponse> getUserRating(Long userId, Long movieId) {
        log.debug("Getting user {} rating for movie {}", userId, movieId);

        return userMovieRatingRepository.findByUserIdAndMovieId(userId, movieId)
            .map(this::convertToDTO);
    }

    @Transactional
    public void deleteRating(Long userId, Long movieId) {
        log.info("Deleting rating for user {} and movie {}", userId, movieId);

        UserMovieRating rating = userMovieRatingRepository.findByUserIdAndMovieId(userId, movieId)
            .orElseThrow(() -> new RuntimeException("Rating not found"));

        userMovieRatingRepository.delete(rating);
        updateMovieAverageRating(movieId);

        log.info("Rating deleted successfully");
    }

    private void updateMovieAverageRating(Long movieId) {
        log.debug("Updating average rating for movie {}", movieId);

        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new RuntimeException("Movie not found"));

        Double averageRating = userMovieRatingRepository.calculateAverageRating(movieId);
        long voteCount = userMovieRatingRepository.countByMovieId(movieId);

        if (averageRating != null) {
            movie.setAverageRating(BigDecimal.valueOf(averageRating)
                .setScale(2, RoundingMode.HALF_UP));
        }
        else {
            movie.setAverageRating(BigDecimal.ZERO);
        }

        movie.setVoteCount((int) voteCount);
        movieRepository.save(movie);
        log.info("Movie {} average rating updated: {} ({} votes)", movieId, movie.getAverageRating(), voteCount);
    }

    private UserRatingResponse convertToDTO(UserMovieRating rating) {
        return UserRatingResponse.builder()
            .id(rating.getId())
            .rating(rating.getRating())
            .createdAt(rating.getCreatedAt())
            .updatedAt(rating.getUpdatedAt())
            .build();
    }
}
