package com.booking.bookingmakeup.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.MakeupArtist;
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

        Booking booking = getBookingById(id);
        if (booking == null) {
            return;
        }

        if (!booking.getUser().getId().equals(user.getId())) {
            return;
        }
        if (!"Pending".equals(booking.getStatus())) {
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

       if (!"Pending".equals(booking.getStatus())) {
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

       if ("Cancelled".equals(booking.getStatus())) {
            return;
        }

        booking.setStatus("Cancelled");

        bookingRepository.save(booking);
    }
    public boolean existsBooking(
            MakeupArtist artist,
            LocalDate date,
            LocalTime time){

        return bookingRepository
                .existsByArtistAndBookingDateAndBookingTime(
                        artist,
                        date,
                        time);
    }

}