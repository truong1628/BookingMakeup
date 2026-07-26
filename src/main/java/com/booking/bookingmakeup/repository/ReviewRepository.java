package com.booking.bookingmakeup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.MakeupService;
import com.booking.bookingmakeup.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByBooking(Booking booking);

    List<Review> findByServiceOrderByCreatedAtDesc(MakeupService service);
    @Query("""
    SELECT AVG(r.rating)
    FROM Review r
    WHERE r.service = :service
    """)
    Double getAverageRating(MakeupService service);
    List<Review> findAllByOrderByCreatedAtDesc();
}