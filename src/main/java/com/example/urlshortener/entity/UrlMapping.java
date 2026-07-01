package com.example.urlshortener.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "url_mappings", indexes = {
    @Index(name = "idx_short_key", columnList = "shortKey", unique = true)
})
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "short_key", nullable = false, unique = true, length = 7)
    private String shortKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "clicks_count", nullable = false)
    private long clicksCount = 0;

    // Constructors
    public UrlMapping() {
        this.createdAt = LocalDateTime.now();
    }

    public UrlMapping(String originalUrl, String shortKey) {
        this.originalUrl = originalUrl;
        this.shortKey = shortKey;
        this.createdAt = LocalDateTime.now();
        this.clicksCount = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortKey() {
        return shortKey;
    }

    public void setShortKey(String shortKey) {
        this.shortKey = shortKey;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getClicksCount() {
        return clicksCount;
    }

    public void setClicksCount(long clicksCount) {
        this.clicksCount = clicksCount;
    }
}
