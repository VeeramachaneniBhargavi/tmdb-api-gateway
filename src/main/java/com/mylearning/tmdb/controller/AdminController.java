package com.mylearning.tmdb.controller;

import com.mylearning.tmdb.model.Movie;
import com.mylearning.tmdb.model.MovieRating;
import com.mylearning.tmdb.model.Rating;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    @Value("${movie-service.url}")
    private String movieServiceUrl;

    @Value("${rating-service.url}")
    private String ratingServiceUrl;

    private RestTemplate restTemplate = new RestTemplate();

    @PostMapping
    public ResponseEntity<Object> addMovie(@RequestBody Movie movie) {

        try{
            log.info("Adding movie");
            Movie savedMovie = restTemplate.postForObject(movieServiceUrl, movie, Movie.class);
            return ResponseEntity.ok().body(savedMovie);
        } catch(HttpStatusCodeException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        try {
            log.info("updating movie: {}", id);
            restTemplate.put(movieServiceUrl + "/" + id, movie);
            return ResponseEntity.ok().build();
        } catch (HttpStatusCodeException ex) {
            log.error("error updating movie: {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> fetchMovieAndRating(@PathVariable Long id) {

        Movie movie;
        try{
            movie = restTemplate.getForObject(movieServiceUrl + "/" + id, Movie.class);
        } catch (HttpStatusCodeException ex) {
            log.error("Error fetching movie {}", ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }

        Rating rating;
        try{
            rating = restTemplate.getForObject(ratingServiceUrl + "/" + movie.getName(), Rating.class);
        } catch (HttpStatusCodeException ex) {
            if(ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                rating = new Rating(null, movie.getName(), 0.0, 0);
            } else{
                rating = new Rating(null, movie.getName(), -1.0, 0);
            }
        } catch (ResourceAccessException ex) {
            log.warn(" Exception {}", ex.getMessage());
            rating = new Rating(null, movie.getName(), -1.0, 0);
        }

        MovieRating movieRating = new MovieRating();
        movieRating.setMovie(movie);
        movieRating.setRating(rating);

        return ResponseEntity.ok(movieRating);
    }
}
