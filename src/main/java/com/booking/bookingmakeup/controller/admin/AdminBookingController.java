package com.booking.bookingmakeup.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.booking.bookingmakeup.service.BookingService;

@Controller
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PutMapping("/admin/bookings/{id}/confirm")
    public String confirmBooking(@PathVariable Long id) {

        bookingService.confirmBooking(id);

        return "redirect:/admin";
    }

    @PutMapping("/admin/bookings/{id}/complete")
    public String completeBooking(@PathVariable Long id) {

        bookingService.completeBooking(id);

        return "redirect:/admin";
    }

    @PutMapping("/admin/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id) {

        bookingService.adminCancelBooking(id);

        return "redirect:/admin";
    }

}