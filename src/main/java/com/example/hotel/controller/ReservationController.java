package com.example.hotel.controller;

import com.example.hotel.model.Reservation;
import com.example.hotel.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin // allows requests from frontend served elsewhere during dev
public class ReservationController {

    private final ReservationService service;

    public ReservationController(ReservationService service) {
        this.service = service;
    }

    @PostMapping("/checkin")
    public ResponseEntity<?> checkIn(@Valid @RequestBody Reservation r) {
        try {
            Reservation saved = service.checkIn(r);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/reservations")
    public List<Reservation> all() {
        return service.getAll();
    }

    @GetMapping("/reservation/{id}")
    public ResponseEntity<?> one(@PathVariable int id) {
        return service.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found"));
    }

    @PutMapping("/reservation/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Reservation r) {
        try {
            return ResponseEntity.ok(service.update(id, r));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/checkout/{id}")
    public ResponseEntity<?> checkout(@PathVariable int id) {
        String result = service.checkOut(id);
        if (result.equals("Reservation not found"))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search/guest")
    public List<Reservation> searchGuest(@RequestParam String name) {
        return service.searchByGuest(name);
    }

    @GetMapping("/search/date")
    public List<Reservation> searchDate(@RequestParam String date) {
        return service.searchByDate(LocalDate.parse(date));
    }
}
