package com.example.hotel.service;

import com.example.hotel.model.Reservation;
import com.example.hotel.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    private final ReservationRepository repo;

    public ReservationService(ReservationRepository repo) {
        this.repo = repo;
    }

    public Reservation checkIn(Reservation r) {
        LocalDate today = LocalDate.now();

        if (r.getContact() == null || r.getContact().length() != 10)
            throw new IllegalArgumentException("Contact must be 10 digits");

        if (r.getPricePerNight() <= 0)
            throw new IllegalArgumentException("Price per night must be positive");

        if (r.getCheckOut() == null || !r.getCheckOut().isAfter(today))
            throw new IllegalArgumentException("Check-out must be a future date (at least tomorrow)");

        // check room availability
        boolean overlap = repo.existsOverlap(r.getRoomNo(), today, r.getCheckOut());
        if (overlap) throw new IllegalStateException("Room is not available for the selected dates");

        r.setCheckIn(today);
        return repo.save(r);
    }

    public List<Reservation> getAll() {
        return repo.findAll();
    }

    public Optional<Reservation> getById(int id) {
        return repo.findById(id);
    }

    public Reservation update(int id, Reservation updated) {
        return repo.findById(id).map(res -> {
            // Basic update: change guest name, contact, room number.
            // If room number or dates change, ideally re-check availability (not fully implemented here).
            res.setGuestName(updated.getGuestName());
            res.setRoomNo(updated.getRoomNo());
            res.setContact(updated.getContact());
            return repo.save(res);
        }).orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    // returns bill amount as string (simple)
    public String checkOut(int id) {
        return repo.findById(id).map(res -> {
            LocalDate checkIn = res.getCheckIn();
            LocalDate planned = res.getCheckOut();
            LocalDate today = LocalDate.now();
            LocalDate finalOut = today.isBefore(planned) ? today : planned;

            long nights = ChronoUnit.DAYS.between(checkIn, finalOut);
            if (nights <= 0) nights = 1;

            double bill = nights * res.getPricePerNight();

            repo.delete(res);
            return String.format("Checked out %s. Nights: %d. Bill: ₹%.2f", res.getGuestName(), nights, bill);
        }).orElse("Reservation not found");
    }

    public List<Reservation> searchByGuest(String guest) {
        return repo.findByGuestNameContainingIgnoreCase(guest);
    }

    public List<Reservation> searchByDate(LocalDate date) {
        return repo.findByCheckInLessThanEqualAndCheckOutGreaterThanEqual(date, date);
    }

    // scheduled cleanup once a day at 03:00
    @Transactional
    @Scheduled(cron = "0 0 3 * * ?")
    public void removeExpiredReservations() {
        int removed = repo.deleteExpired();
        if (removed > 0) {
            System.out.println("Removed expired reservations: " + removed);
        }
    }
}
