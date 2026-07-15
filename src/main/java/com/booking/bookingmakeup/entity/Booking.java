package com.booking.bookingmakeup.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Vui lòng chọn ngày")
    @FutureOrPresent(message = "Ngày phải từ hôm nay trở đi")
    private LocalDate bookingDate;

    @NotNull(message = "Vui lòng chọn giờ")
    private LocalTime bookingTime;
  

    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private MakeupService service;

    @NotNull(message = "Vui lòng chọn Makeup Artist")
    @ManyToOne
    @JoinColumn(name = "artist_id")
    private MakeupArtist artist;

    public Booking() {
    }

    public Long getId() {
        return id;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MakeupService getService() {
        return service;
    }

    public void setService(MakeupService service) {
        this.service = service;
    }
    public MakeupArtist getArtist() {
        return artist;
    }

    public void setArtist(MakeupArtist artist) {
        this.artist = artist;
    }
}