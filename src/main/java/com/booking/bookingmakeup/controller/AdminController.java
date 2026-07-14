package com.booking.bookingmakeup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.BookingService;
import com.booking.bookingmakeup.service.ServiceService;

import jakarta.servlet.http.HttpSession;


@Controller
public class AdminController {

    private final BookingService bookingService;
    private final ServiceService serviceService; 
    public AdminController(BookingService bookingService, ServiceService serviceService) {
        this.bookingService = bookingService;
        this.serviceService = serviceService;
    }

    @GetMapping("/admin")
    public String admin(HttpSession session, Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!loginUser.getRole().equals("ADMIN")) {
            return "redirect:/";
        }

        model.addAttribute(
                "bookings",
                bookingService.getAllBookings());

        return "admin";
    }

    @PutMapping("/admin/booking/{id}/confirm")
public String confirmBooking(
        @PathVariable Long id,
        HttpSession session) {

    User loginUser =
            (User) session.getAttribute("loginUser");

    if (loginUser == null) {
        return "redirect:/login";
    }

    if (!loginUser.getRole().equals("ADMIN")) {
        return "redirect:/";
    }

    bookingService.confirmBooking(id);

    return "redirect:/admin";
}

    @PutMapping("/admin/booking/{id}/cancel")
    public String cancelBooking(
            @PathVariable Long id,
            HttpSession session) {

        User loginUser =
                (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!loginUser.getRole().equals("ADMIN")) {
            return "redirect:/";
        }

        bookingService.adminCancelBooking(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/services")
    public String adminServices(
            HttpSession session,
            Model model) {

        User loginUser =
                (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!loginUser.getRole().equals("ADMIN")) {
            return "redirect:/";
        }

        model.addAttribute(
                "services",
                serviceService.getAllServices());

        return "admin-services";
    }
   
}