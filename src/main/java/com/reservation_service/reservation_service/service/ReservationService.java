package com.reservation_service.reservation_service.service;

import com.reservation_service.reservation_service.model.BookResponseDTO;
import com.reservation_service.reservation_service.model.Reservation;
import com.reservation_service.reservation_service.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookServiceClient bookClient;

    public ReservationService(ReservationRepository repository, BookServiceClient bookClient) {
        this.reservationRepository = repository;
        this.bookClient = bookClient;
    }

    public Reservation createReservation(Reservation reservation) {
        BookResponseDTO livro = bookClient.buscarLivroPorId(reservation.getBookId());
        if (livro == null || !"disponível".equalsIgnoreCase(livro.getStatus())) {
            throw new RuntimeException("Livro não disponível para reserva.");
        }

        reservation.setStatus("ativa");
        reservation.setDataReserva(LocalDate.now());
        Reservation saved = reservationRepository.save(reservation);
        bookClient.atualizarStatusLivro(reservation.getBookId(), "reservado");
        return saved;
    }

    public void cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva não encontrada"));
        reservationRepository.deleteById(id);
        bookClient.atualizarStatusLivro(reservation.getBookId(), "disponível");
    }

    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }
}
