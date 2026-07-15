package com.booking.bookingmakeup.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.MakeupArtist;
import com.booking.bookingmakeup.entity.User;

public interface BookingRepository extends JpaRepository<Booking, Long>{

    List<Booking> findByUser(User user);
    boolean existsByArtistAndBookingDateAndBookingTime(
        MakeupArtist artist,
        LocalDate bookingDate,
        LocalTime bookingTime);
}