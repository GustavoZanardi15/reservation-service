package com.reservation_service.reservation_service.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Component
public class BookClient {

    private final RestTemplate restTemplate;
    private final String bookServiceUrl = "http://localhost:8080/books";

    public BookClient() {
        this.restTemplate = new RestTemplate();
    }

    public boolean isBookAvailable(Long bookId) {
        try {
            Map book = restTemplate.getForObject(bookServiceUrl + "/" + bookId, Map.class);
            return book != null && "disponível".equalsIgnoreCase((String) book.get("status"));
        } catch (Exception e) {
            return false;
        }
    }

    public void updateBookStatus(Long bookId, String status) {
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("status", status));
        restTemplate.exchange(
                bookServiceUrl + "/" + bookId + "/status",
                HttpMethod.PATCH,
                request,
                Void.class
        );
    }
}
