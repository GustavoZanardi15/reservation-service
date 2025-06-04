package com.reservation_service.reservation_service.controller;

import com.reservation_service.reservation_service.model.Reservation;
import com.reservation_service.reservation_service.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping
    public Reservation criarReserva(@RequestBody Reservation reserva) {
        return reservationService.criarReserva(reserva);
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> listarReservasPorUsuario(@PathVariable Long userId) {
        return reservationService.listarReservasPorUsuario(userId);
    }

    @DeleteMapping("/{id}")
    public void cancelarReserva(@PathVariable Long id) {
        reservationService.cancelarReserva(id);
    }
}
