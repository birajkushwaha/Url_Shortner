package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlShortenRequest;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import com.example.urlshortener.util.Base62Generator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository repository;

    @Autowired
    public UrlShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UrlResponse shortenUrl(UrlShortenRequest request, String baseUrl) {
        String shortKey = generateUniqueKey();
        
        UrlMapping urlMapping = new UrlMapping(request.getOriginalUrl(), shortKey);
        UrlMapping saved = repository.save(urlMapping);
        
        return mapToResponse(saved, baseUrl);
    }

    @Transactional
    public String getOriginalUrlAndIncrementClick(String shortKey) {
        UrlMapping mapping = repository.findByShortKey(shortKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found"));
        
        mapping.setClicksCount(mapping.getClicksCount() + 1);
        repository.save(mapping);
        
        return mapping.getOriginalUrl();
    }

    @Transactional(readOnly = true)
    public UrlResponse getAnalytics(String shortKey, String baseUrl) {
        UrlMapping mapping = repository.findByShortKey(shortKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found"));
        
        return mapToResponse(mapping, baseUrl);
    }

    private String generateUniqueKey() {
        int maxAttempts = 10;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String key = Base62Generator.generate();
            if (!repository.existsByShortKey(key)) {
                return key;
            }
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate unique short URL key. Please try again.");
    }

    private UrlResponse mapToResponse(UrlMapping mapping, String baseUrl) {
        String shortUrl = baseUrl + "/" + mapping.getShortKey();
        return new UrlResponse(
                mapping.getOriginalUrl(),
                shortUrl,
                mapping.getShortKey(),
                mapping.getCreatedAt(),
                mapping.getClicksCount()
        );
    }
}
