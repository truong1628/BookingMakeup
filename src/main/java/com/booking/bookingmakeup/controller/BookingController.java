package com.booking.bookingmakeup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.MakeupService;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.BookingService;
import com.booking.bookingmakeup.service.ServiceService;

import jakarta.servlet.http.HttpSession;

@Controller
public class BookingController {

    private final ServiceService serviceService;
    private final BookingService bookingService;

    public BookingController(ServiceService serviceService,
                            BookingService bookingService) {

        this.serviceService = serviceService;
        this.bookingService = bookingService;
    }

    @GetMapping("/booking/{id}")
    public String bookingPage(
            @PathVariable Long id,
            Model model) {

        Booking booking = new Booking();

        model.addAttribute("booking", booking);

        model.addAttribute(
                "service",
                serviceService.getServiceById(id));

        return "booking";
    }

    @PostMapping("/booking")
    public String saveBooking(
            @ModelAttribute Booking booking,
            @RequestParam Long serviceId,
            HttpSession session) {

        // Lấy user đang đăng nhập
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        // Lấy dịch vụ
        MakeupService service = serviceService.getServiceById(serviceId);

        booking.setUser(loginUser);
        booking.setService(service);
        booking.setStatus("Pending");

        bookingService.save(booking);

        return "redirect:/";
    }

    @GetMapping("/MyBooking")
    public String myBookings(
            HttpSession session,
            Model model){

        User loginUser =
            (User) session.getAttribute("loginUser");

        if(loginUser == null){
            return "redirect:/login";
        }

        model.addAttribute(
                "bookings",
                bookingService.getBookingsByUser(loginUser));

        return "MyBooking";
    }

    @GetMapping("/booking/cancel/{id}")
    public String cancelBooking(
            @PathVariable Long id,
            HttpSession session) {

        User loginUser =
                (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        bookingService.cancelBooking(id);

        return "redirect:/MyBooking";
    }
}