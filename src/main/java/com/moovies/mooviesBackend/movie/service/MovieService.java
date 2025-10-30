package com.moovies.mooviesBackend.movie.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moovies.mooviesBackend.movie.dto.tmdb.TMDBGenreDTO;
import com.moovies.mooviesBackend.movie.dto.tmdb.TMDBMovieDTO;
import com.moovies.mooviesBackend.movie.dto.tmdb.TMDBSearchResponseDTO;
import com.moovies.mooviesBackend.movie.entity.Movie;
import com.moovies.mooviesBackend.movie.repository.MovieRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final TMDBService tmdbService;

    public MovieService(MovieRepository movieRepository, TMDBService tmdbService) {
        this.movieRepository = movieRepository;
        this.tmdbService = tmdbService;
    }

    @Transactional
    public Movie getOrCacheMovie(Long tmdbId) {
        log.info("Getting or caching movie with TMDB ID: {}", tmdbId);

        Optional<Movie> existingMovie = movieRepository.findByTmdbId(tmdbId);

        if (existingMovie.isPresent()) {
            log.info("Movie found in cache: {}", existingMovie.get().getTitle());
            return existingMovie.get();
        }

        log.info("Movie not found in cache, fetching from TMDB: {}", tmdbId);
        TMDBMovieDTO movieDetails = tmdbService.getMovieDetails(tmdbId);

        if (movieDetails == null) {
            log.error("Movie not found for TMDB ID: {}", tmdbId);
            throw new RuntimeException("Movie not found for TMDB ID: " + tmdbId);
        }

        Movie movie = convertToEntity(movieDetails);

        Movie saveMovie = movieRepository.save(movie);
        log.info("Movie saved to database: {}", saveMovie.getTitle());

        return saveMovie;
    }

    public TMDBSearchResponseDTO searchMovies(String query, int page) {
        log.info("Searching movies: query={}, page={}", query, page);
        return tmdbService.searchMovies(query, page);
    }

    public Movie getMovieById(Long id) {
        log.info("Getting movie by ID: {}" , id);
        return movieRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movie not found with ID: " + id));
    }

    public Optional<Movie> getCachedMovieByTmdbId(Long tmdbId) {
        log.info("Getting movie by TMDB ID: {}", tmdbId);
        return movieRepository.findByTmdbId(tmdbId);
    }

    public boolean isMovieCached(Long tmdbId) {
        return movieRepository.existsByTmdbId(tmdbId);
    }

    private Movie convertToEntity(TMDBMovieDTO tmdbMovie) {
        log.debug("Converting TMDB DTO to Movie entity: {}", tmdbMovie.getTitle());

        return Movie.builder()
            .tmdbId(tmdbMovie.getId())
            .title(tmdbMovie.getTitle())
            .overview(tmdbMovie.getOverview())
            .posterPath(tmdbMovie.getPosterPath())
            .backdropPath(tmdbMovie.getBackdropPath())
            .releaseDate(parseReleaseDate(tmdbMovie.getReleaseDate()))
            .runtime(tmdbMovie.getRuntime())
            .genres(convertGenresToString(tmdbMovie.getGenres()))
            .build();
    }

    private LocalDate parseReleaseDate(String releaseDate) {
        if (releaseDate == null || releaseDate.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(releaseDate);
        } catch (Exception e) {
            log.warn("Failed to parse release date: {}", releaseDate);
            return null;
        }
    }

    private String convertGenresToString(List<TMDBGenreDTO> genres) {
        if (genres == null || genres.isEmpty()) {
            return null;
        }

        return genres.stream()
            .map(TMDBGenreDTO::getName)
            .collect(Collectors.joining(","));
    }
}
