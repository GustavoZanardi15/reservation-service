package com.reservation_service.reservation_service.controller;

import com.reservation_service.reservation_service.model.Reservation;
import com.reservation_service.reservation_service.model.BookResponseDTO;
import com.reservation_service.reservation_service.repository.ReservationRepository;
import com.reservation_service.reservation_service.service.BookServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BookServiceClient bookServiceClient;

    @PostMapping
    public ResponseEntity<?> criarReserva(@RequestBody Reservation reservation) {
        BookResponseDTO livro = bookServiceClient.buscarLivroPorId(reservation.getBookId());
        if (livro == null || !"disponível".equalsIgnoreCase(livro.getStatus())) {
            return ResponseEntity.badRequest().body("Livro não disponível para reserva.");
        }
        Reservation novaReserva = reservationRepository.save(reservation);
        bookServiceClient.atualizarStatusLivro(reservation.getBookId(), "reservado");

        return ResponseEntity.ok(novaReserva);
    }
}
