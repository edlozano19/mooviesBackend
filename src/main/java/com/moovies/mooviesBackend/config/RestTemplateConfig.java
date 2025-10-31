package com.moovies.mooviesBackend.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new TMDBAuthInterceptor(tmdbApiKey)));
        return restTemplate;
    }

    private static class TMDBAuthInterceptor implements ClientHttpRequestInterceptor {
        private final String apiKey;        

        public TMDBAuthInterceptor(String apiKey) {
            this.apiKey = apiKey;
        }

        @Override
        public ClientHttpResponse intercept (
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            
            request.getHeaders().add("Authorization", "Bearer " + apiKey);
            request.getHeaders().add("accept", "application/json");

            return execution.execute(request, body);
        }
    }
}
