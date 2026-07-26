package com.booking.bookingmakeup.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.MakeupService;
import com.booking.bookingmakeup.entity.Review;
import com.booking.bookingmakeup.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review save(Review review) {

        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    public boolean hasReview(Booking booking) {
        return reviewRepository.existsByBooking(booking);
    }

    public List<Review> getReviewsByService(MakeupService service) {
        return reviewRepository.findByServiceOrderByCreatedAtDesc(service);
    }

    public Double getAverageRating(MakeupService service) {

    Double avg = reviewRepository.getAverageRating(service);

        return avg == null ? 0.0 : avg;

    }
    
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public void delete(Long id) {

        Review review = reviewRepository.findById(id).orElse(null);

        if (review != null) {
            reviewRepository.delete(review);
        }
    }
    public long countReviews() {
        return reviewRepository.count();
    }
        
}