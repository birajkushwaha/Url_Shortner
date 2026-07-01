package com.example.urlshortener.service;

import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlShortenRequest;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository repository;

    @InjectMocks
    private UrlShortenerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShortenUrl_Success() {
        UrlShortenRequest request = new UrlShortenRequest("https://example.com");
        String baseUrl = "http://localhost:8080";

        when(repository.existsByShortKey(anyString())).thenReturn(false);
        
        UrlMapping savedMapping = new UrlMapping(request.getOriginalUrl(), "abc1234");
        when(repository.save(any(UrlMapping.class))).thenReturn(savedMapping);

        UrlResponse response = service.shortenUrl(request, baseUrl);

        assertNotNull(response);
        assertEquals("https://example.com", response.getOriginalUrl());
        assertEquals("http://localhost:8080/abc1234", response.getShortUrl());
        assertEquals("abc1234", response.getShortKey());
        assertEquals(0, response.getClicksCount());
        
        verify(repository, times(1)).save(any(UrlMapping.class));
    }

    @Test
    void testGetOriginalUrlAndIncrementClick_Success() {
        String shortKey = "abc1234";
        UrlMapping mapping = new UrlMapping("https://example.com", shortKey);
        mapping.setClicksCount(5);

        when(repository.findByShortKey(shortKey)).thenReturn(Optional.of(mapping));
        when(repository.save(any(UrlMapping.class))).thenReturn(mapping);

        String originalUrl = service.getOriginalUrlAndIncrementClick(shortKey);

        assertEquals("https://example.com", originalUrl);
        assertEquals(6, mapping.getClicksCount());
        verify(repository, times(1)).save(mapping);
    }

    @Test
    void testGetOriginalUrlAndIncrementClick_NotFound() {
        String shortKey = "missing";
        when(repository.findByShortKey(shortKey)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            service.getOriginalUrlAndIncrementClick(shortKey);
        });
        
        verify(repository, never()).save(any(UrlMapping.class));
    }
}
