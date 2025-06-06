package com.reservation_service.reservation_service.controller;

import com.reservation_service.reservation_service.model.Reservation;
import com.reservation_service.reservation_service.model.BookResponseDTO;
import com.reservation_service.reservation_service.repository.ReservationRepository;
import com.reservation_service.reservation_service.service.BookServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listarReservasPorUsuario(@PathVariable Long userId) {
        List<Reservation> reservas = reservationRepository.findByUserId(userId);
        if (reservas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        Reservation reserva = reservationRepository.findById(id).orElse(null);
        if (reserva == null) {
            return ResponseEntity.notFound().build();
        }

        reservationRepository.deleteById(id);
        bookServiceClient.atualizarStatusLivro(reserva.getBookId(), "disponível");

        return ResponseEntity.ok("Reserva cancelada com sucesso.");
    }


}
