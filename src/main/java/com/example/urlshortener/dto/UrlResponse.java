package com.example.urlshortener.dto;

import java.time.LocalDateTime;

public class UrlResponse {

    private String originalUrl;
    private String shortUrl;
    private String shortKey;
    private LocalDateTime createdAt;
    private long clicksCount;

    public UrlResponse() {}

    public UrlResponse(String originalUrl, String shortUrl, String shortKey, LocalDateTime createdAt, long clicksCount) {
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.shortKey = shortKey;
        this.createdAt = createdAt;
        this.clicksCount = clicksCount;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
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
