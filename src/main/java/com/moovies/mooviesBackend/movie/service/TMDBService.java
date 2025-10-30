package com.moovies.mooviesBackend.movie.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.moovies.mooviesBackend.movie.dto.tmdb.TMDBMovieDTO;
import com.moovies.mooviesBackend.movie.dto.tmdb.TMDBSearchResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TMDBService {
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;
    private final String imageBaseUrl;

    public TMDBService(
            RestTemplate restTemplate,
            @Value("${tmdb.api.key}") String apiKey,
            @Value("${tmdb.api.base-url}") String baseUrl,
            @Value("${tmdb.api.image-base-url}") String imageBaseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.imageBaseUrl = imageBaseUrl;
    }

    public TMDBSearchResponseDTO searchMovies(String query, int page) {
        log.info("Searching TMDB for movies: query={}, page={}", query, page);

        String url = UriComponentsBuilder
            .fromUriString(baseUrl + "/search/movie")
            .queryParam("api_key", apiKey)
            .queryParam("query", query)
            .queryParam("page", page)
            .queryParam("language", "en-US")
            .toUriString();

        TMDBSearchResponseDTO response = restTemplate.getForObject(url, TMDBSearchResponseDTO.class);
        log.info("TMDB search returned {} results",
            response != null ? response.getResults().size() : 0);
            
        return response;
    }

    public TMDBMovieDTO getMovieDetails(Long tmdbId) {
        log.info("Fetching TMDB movie details: tmdbId={}", tmdbId);

        String url = UriComponentsBuilder
            .fromUriString(baseUrl + "/movie/" + tmdbId)
            .queryParam("api_key", apiKey)
            .queryParam("language", "en-US")
            .toUriString();

        TMDBMovieDTO movie = restTemplate.getForObject(url, TMDBMovieDTO.class);
        log.info("TMDB returned movie: {}", movie != null ? movie.getTitle() : "null");

        return movie;
    }

    public String getPosterUrl(String posterPath, String imageSize) {
        if (posterPath == null || posterPath.isEmpty()) {
            return null;
        }
        return imageBaseUrl + imageSize + posterPath;
    }

    public String getBackdropUrl(String backdropPath, String imageSize) {
        if (backdropPath == null || backdropPath.isEmpty()) {
            return null;
        }
        return imageBaseUrl + imageSize + backdropPath;
    }
}
