package com.example.urlshortener.controller;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlShortenRequest;
import com.example.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class UrlController {

    private final UrlShortenerService service;

    @Autowired
    public UrlController(UrlShortenerService service) {
        this.service = service;
    }

    @PostMapping("/api/v1/urls/shorten")
    public ResponseEntity<UrlResponse> shortenUrl(
            @Valid @RequestBody UrlShortenRequest request,
            HttpServletRequest servletRequest) {
        
        String baseUrl = getBaseUrl(servletRequest);
        UrlResponse response = service.shortenUrl(request, baseUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirectToOriginal(
            @PathVariable String shortKey) {
        
        String originalUrl = service.getOriginalUrlAndIncrementClick(shortKey);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, originalUrl)
                .build();
    }

    @GetMapping("/api/v1/urls/{shortKey}/analytics")
    public ResponseEntity<UrlResponse> getAnalytics(
            @PathVariable String shortKey,
            HttpServletRequest servletRequest) {
        
        String baseUrl = getBaseUrl(servletRequest);
        UrlResponse response = service.getAnalytics(shortKey, baseUrl);
        return ResponseEntity.ok(response);
    }

    private String getBaseUrl(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        String servletPath = request.getServletPath();
        return requestUrl.replace(servletPath, "");
    }
}
