package com.booking.bookingmakeup.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.repository.BookingRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(User user){
        return bookingRepository.findByUser(user);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    public void cancelBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking != null) {

            booking.setStatus("Cancelled");

            bookingRepository.save(booking);

        }

    }

    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();

    }
    public void confirmBooking(Long id) {

        Booking booking =
                bookingRepository.findById(id).orElse(null);

        if (booking != null) {

            booking.setStatus("Confirmed");

            bookingRepository.save(booking);

        }

}
}