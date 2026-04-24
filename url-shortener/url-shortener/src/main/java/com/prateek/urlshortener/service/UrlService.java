package com.prateek.urlshortener.service;

import com.prateek.urlshortener.dto.UrlDto;
import com.prateek.urlshortener.exception.UrlNotFoundException;
import com.prateek.urlshortener.exception.UrlExpiredException;
import com.prateek.urlshortener.model.Url;
import com.prateek.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.short-url-length}")
    private int shortUrlLength;

    // Base62 charset for encoding
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── Shorten URL ──────────────────────────────────────────
    public UrlDto.ShortenResponse shortenUrl(UrlDto.ShortenRequest request) {
        // Return existing short URL if already shortened
        return urlRepository.findByOriginalUrl(request.getOriginalUrl())
                .filter(url -> url.getActive() &&
                        (url.getExpiresAt() == null || url.getExpiresAt().isAfter(LocalDateTime.now())))
                .map(this::toShortenResponse)
                .orElseGet(() -> createNewShortUrl(request));
    }

    // ── Redirect ─────────────────────────────────────────────
    public String getOriginalUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        if (!url.getActive()) {
            throw new UrlNotFoundException("This short URL has been deactivated.");
        }

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("This short URL has expired.");
        }

        urlRepository.incrementClickCount(shortCode);
        return url.getOriginalUrl();
    }

    // ── Stats ─────────────────────────────────────────────────
    public UrlDto.StatsResponse getStats(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        UrlDto.StatsResponse stats = new UrlDto.StatsResponse();
        stats.setOriginalUrl(url.getOriginalUrl());
        stats.setShortUrl(baseUrl + "/" + shortCode);
        stats.setClickCount(url.getClickCount());
        stats.setCreatedAt(url.getCreatedAt().format(FORMATTER));
        stats.setExpiresAt(url.getExpiresAt() != null ? url.getExpiresAt().format(FORMATTER) : "Never");
        stats.setActive(url.getActive());
        return stats;
    }

    // ── Get All ───────────────────────────────────────────────
    public List<UrlDto.ShortenResponse> getAllUrls() {
        return urlRepository.findAll().stream()
                .map(this::toShortenResponse)
                .toList();
    }

    // ── Delete ────────────────────────────────────────────────
    public void deleteUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));
        urlRepository.delete(url);
    }

    // ── Private helpers ───────────────────────────────────────
    private UrlDto.ShortenResponse createNewShortUrl(UrlDto.ShortenRequest request) {
        String shortCode = generateUniqueShortCode();

        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .expiresAt(request.getExpiryDays() != null
                        ? LocalDateTime.now().plusDays(request.getExpiryDays())
                        : null)
                .build();

        urlRepository.save(url);
        return toShortenResponse(url);
    }

    private String generateUniqueShortCode() {
        String shortCode;
        int attempts = 0;
        do {
            shortCode = generateBase62Code();
            attempts++;
            if (attempts > 10) throw new RuntimeException("Could not generate unique short code");
        } while (urlRepository.existsByShortCode(shortCode));
        return shortCode;
    }

    // Base62 encoding — collision-resistant short code generation
    private String generateBase62Code() {
        long id = System.nanoTime();
        StringBuilder sb = new StringBuilder();
        while (id > 0 && sb.length() < shortUrlLength) {
            sb.append(BASE62.charAt((int)(id % 62)));
            id /= 62;
        }
        // Pad if needed
        while (sb.length() < shortUrlLength) {
            sb.append(BASE62.charAt((int)(Math.random() * 62)));
        }
        return sb.toString();
    }

    private UrlDto.ShortenResponse toShortenResponse(Url url) {
        UrlDto.ShortenResponse response = new UrlDto.ShortenResponse();
        response.setOriginalUrl(url.getOriginalUrl());
        response.setShortUrl(baseUrl + "/" + url.getShortCode());
        response.setShortCode(url.getShortCode());
        response.setClickCount(url.getClickCount());
        response.setCreatedAt(url.getCreatedAt().format(FORMATTER));
        response.setExpiresAt(url.getExpiresAt() != null ? url.getExpiresAt().format(FORMATTER) : "Never");
        return response;
    }
}
