package com.booking.bookingmakeup.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.booking.bookingmakeup.entity.MakeupArtist;

public interface MakeupArtistRepository
        extends JpaRepository<MakeupArtist,Long>{
        Optional<MakeupArtist> findByEmail(String email);

        Optional<MakeupArtist> findByEmailAndPassword(
                String email,
                String password);
        List<MakeupArtist> findByActiveTrue();

        @Query("""
        SELECT a
        FROM MakeupArtist a
        WHERE a.active = true
        AND a.id NOT IN (
        SELECT b.artist.id
        FROM Booking b
        WHERE b.bookingDate = :date
        AND b.bookingTime = :time
        AND b.artist IS NOT NULL
        AND b.status <> 'CANCELLED'
        )
        """)
        List<MakeupArtist> findAvailableArtists(
        @Param("date") LocalDate date,
        @Param("time") LocalTime time);
}