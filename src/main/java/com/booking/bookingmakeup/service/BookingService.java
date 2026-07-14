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

    // User hủy lịch
    public void cancelBooking(Long id, User user) {

        Booking booking = bookingRepository.findById(id).orElse(null);

        if (booking == null) {
            return;
        }

        // Chỉ được hủy booking của chính mình
        if (!booking.getUser().getId().equals(user.getId())) {
            return;
        }
        if (!booking.getStatus().equals("Pending")) {
            return;
        }

        booking.setStatus("Cancelled");

        bookingRepository.save(booking);
    }

    

    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();

    }

       // Admin xác nhận
    public void confirmBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking == null) {
            return;
        }

        // Chỉ Pending mới được Confirm
        if (!booking.getStatus().equals("Pending")) {
            return;
        }

        booking.setStatus("Confirmed");

        bookingRepository.save(booking);
    }

 // Admin hủy
    public void adminCancelBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking == null) {
            return;
        }

        // Nếu đã hủy thì thôi
        if (booking.getStatus().equals("Cancelled")) {
            return;
        }

        booking.setStatus("Cancelled");

        bookingRepository.save(booking);
    }

}