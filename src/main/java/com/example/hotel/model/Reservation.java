package com.example.hotel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Integer reservationId;

    @NotBlank
    @Column(name = "guest_name")
    private String guestName;

    @Column(name = "room_no")
    private int roomNo;

    @Pattern(regexp = "^[0-9]{10}$")
    @Column(name = "contact")
    private String contact;

    @NotBlank
    @Column(name = "room_type")
    private String roomType;

    @Positive
    @Column(name = "price_per_night")
    private double pricePerNight;

    @Column(name = "check_in")
    private LocalDate checkIn;

    @Column(name = "check_out")
    private LocalDate checkOut;

    @Column(name = "reservation_date", updatable = false, insertable = false)
    private java.sql.Timestamp reservationDate;

    // Getters and setters (generated)
    public Integer getReservationId() { return reservationId; }
    public void setReservationId(Integer reservationId) { this.reservationId = reservationId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public int getRoomNo() { return roomNo; }
    public void setRoomNo(int roomNo) { this.roomNo = roomNo; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }
    public java.sql.Timestamp getReservationDate() { return reservationDate; }
}
