package com.prateek.urlshortener.controller;

import com.prateek.urlshortener.dto.UrlDto;
import com.prateek.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UrlController {

    private final UrlService urlService;

    // POST /api/shorten — create short URL
    @PostMapping("/api/shorten")
    public ResponseEntity<UrlDto.ShortenResponse> shortenUrl(
            @Valid @RequestBody UrlDto.ShortenRequest request) {
        UrlDto.ShortenResponse response = urlService.shortenUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /{shortCode} — redirect to original URL
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    // GET /api/stats/{shortCode} — get URL stats
    @GetMapping("/api/stats/{shortCode}")
    public ResponseEntity<UrlDto.StatsResponse> getStats(@PathVariable String shortCode) {
        return ResponseEntity.ok(urlService.getStats(shortCode));
    }

    // GET /api/urls — get all URLs
    @GetMapping("/api/urls")
    public ResponseEntity<List<UrlDto.ShortenResponse>> getAllUrls() {
        return ResponseEntity.ok(urlService.getAllUrls());
    }

    // DELETE /api/delete/{shortCode} — delete a URL
    @DeleteMapping("/api/delete/{shortCode}")
    public ResponseEntity<Map<String, String>> deleteUrl(@PathVariable String shortCode) {
        urlService.deleteUrl(shortCode);
        return ResponseEntity.ok(Map.of("message", "URL deleted successfully"));
    }

    // GET /api/health — health check
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "URL Shortener API"));
    }
}
