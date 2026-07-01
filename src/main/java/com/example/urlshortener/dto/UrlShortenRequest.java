package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UrlShortenRequest {

    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^https?://.+", message = "URL must start with http:// or https://")
    private String originalUrl;

    public UrlShortenRequest() {}

    public UrlShortenRequest(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
}
