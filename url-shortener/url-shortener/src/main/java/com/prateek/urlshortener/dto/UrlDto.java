package com.prateek.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

public class UrlDto {

    @Data
    public static class ShortenRequest {
        @NotBlank(message = "URL cannot be blank")
        @Pattern(regexp = "^(https?://).*", message = "URL must start with http:// or https://")
        private String originalUrl;

        private Integer expiryDays; // optional, null = never expires
    }

    @Data
    public static class ShortenResponse {
        private String originalUrl;
        private String shortUrl;
        private String shortCode;
        private String expiresAt;
        private Long clickCount;
        private String createdAt;
    }

    @Data
    public static class StatsResponse {
        private String originalUrl;
        private String shortUrl;
        private Long clickCount;
        private String createdAt;
        private String expiresAt;
        private Boolean active;
    }
}
