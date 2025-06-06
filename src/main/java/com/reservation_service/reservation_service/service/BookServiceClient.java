package com.reservation_service.reservation_service.service;

import com.reservation_service.reservation_service.model.BookResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class BookServiceClient {

    private final RestTemplate restTemplate;
    private final String bookServiceUrl = "http://localhost:8081/books";

    @Autowired
    public BookServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BookResponseDTO buscarLivroPorId(Long id) {
        return restTemplate.getForObject(bookServiceUrl + "/" + id, BookResponseDTO.class);
    }

    public void atualizarStatusLivro(Long id, String novoStatus) {
        String url = bookServiceUrl + "/" + id + "/status";
        Map<String, String> payload = Map.of("status", novoStatus);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload);
        restTemplate.exchange(url, HttpMethod.PATCH, request, Void.class);
    }

}
