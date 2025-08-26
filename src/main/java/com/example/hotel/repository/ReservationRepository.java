package com.example.hotel.repository;

import com.example.hotel.model.Reservation;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByGuestNameContainingIgnoreCase(String guestName);
    List<Reservation> findByCheckInLessThanEqualAndCheckOutGreaterThanEqual(LocalDate d1, LocalDate d2);

    // <-- REPLACE the native EXISTS method with this JPQL one:
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
           "FROM Reservation r " +
           "WHERE r.roomNo = :room " +
           "  AND :checkIn < r.checkOut " +
           "  AND :checkOut > r.checkIn")
    boolean existsOverlap(@Param("room") int room,
                          @Param("checkIn") LocalDate checkIn,
                          @Param("checkOut") LocalDate checkOut);

    @Modifying
    @Query(value = "DELETE FROM reservations WHERE check_out < CURDATE()", nativeQuery = true)
    int deleteExpired();
}
