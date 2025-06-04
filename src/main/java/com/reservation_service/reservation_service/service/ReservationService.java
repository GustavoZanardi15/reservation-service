package com.reservation_service.reservation_service.service;

import com.reservation_service.reservation_service.model.Reservation;
import com.reservation_service.reservation_service.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private final String BOOK_SERVICE_URL = "http://localhost:8080/books/";

    public Reservation criarReserva(Reservation reserva) {
        ResponseEntity<BookDTO> resposta = restTemplate.getForEntity(BOOK_SERVICE_URL + reserva.getBookId(), BookDTO.class);

        if (resposta.getStatusCode().is2xxSuccessful() && resposta.getBody() != null) {
            BookDTO livro = resposta.getBody();

            if ("disponível".equalsIgnoreCase(livro.getStatus())) {
                livro.setStatus("reservado");
                restTemplate.put(BOOK_SERVICE_URL + reserva.getBookId() + "/status", livro);

                return reservationRepository.save(reserva);
            } else {
                throw new IllegalStateException("Livro não está disponível para reserva.");
            }
        } else {
            throw new RuntimeException("Erro ao consultar o book-service.");
        }
    }

    public List<Reservation> listarReservasPorUsuario(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public void cancelarReserva(Long id) {
        Optional<Reservation> reserva = reservationRepository.findById(id);
        if (reserva.isPresent()) {
            Reservation r = reserva.get();
            r.setStatus("cancelada");
            reservationRepository.save(r);

            BookDTO livro = new BookDTO();
            livro.setStatus("disponível");
            restTemplate.put(BOOK_SERVICE_URL + r.getBookId() + "/status", livro);
        } else {
            throw new RuntimeException("Reserva não encontrada.");
        }
    }

    private static class BookDTO {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
