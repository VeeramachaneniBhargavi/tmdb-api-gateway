package com.mylearning.tmdb.controller;

import com.mylearning.tmdb.model.Rating;
import com.mylearning.tmdb.model.RatingRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/public")
@Slf4j
public class PublicController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${rating-service.url}")
    private String ratingServiceUrl;

    @PostMapping
    public ResponseEntity<Object> addRating(@RequestBody RatingRequest ratingRequest) {

        Rating savedRating;
        try{
            log.info("adding rating");
            savedRating = restTemplate.postForObject(ratingServiceUrl, ratingRequest, Rating.class);
            return ResponseEntity.ok(savedRating);
        } catch (HttpStatusCodeException ex) {
            log.error("error while adding rating {}", ex.getStatusCode());
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }


    }

}
