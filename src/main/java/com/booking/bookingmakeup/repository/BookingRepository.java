package com.booking.bookingmakeup.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.MakeupArtist;
import com.booking.bookingmakeup.entity.User;

public interface BookingRepository extends JpaRepository<Booking, Long>{

    List<Booking> findByUser(User user);
    List<Booking> findByArtistIdOrderByBookingDateAscBookingTimeAsc(Long artistId);    boolean existsByArtistAndBookingDateAndBookingTime(
        MakeupArtist artist,
        LocalDate bookingDate,
        LocalTime bookingTime);
    @Query("""
        select b.bookingTime
        from Booking b
        where b.artist = :artist
        and b.bookingDate = :date
        and b.status <> 'Cancelled'
        """)
    List<LocalTime> findBookedTimesByArtistAndDate(
                @Param("artist") MakeupArtist artist,
                @Param("date") LocalDate date);
    long countByStatus(String status);   

    boolean existsByArtistAndBookingDateAndBookingTimeAndIdNot(
            MakeupArtist artist,
            LocalDate bookingDate,
            LocalTime bookingTime,
            Long id);

    List<Booking> findByArtistId(Long artistId);
    List<Booking> findByArtistIdOrderByIdDesc(Long artistId);
}