package com.booking.bookingmakeup.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.BookingService;

import jakarta.servlet.http.HttpSession;


@Controller
public class AdminController {

    private final BookingService bookingService;

    public AdminController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/admin")
    public String admin(HttpSession session, Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("totalBookings", bookingService.countAll());
        model.addAttribute("pendingBookings", bookingService.countPending());
        model.addAttribute("confirmedBookings", bookingService.countConfirmed());
        model.addAttribute("completedBookings", bookingService.countCompleted());
        model.addAttribute("cancelledBookings", bookingService.countCancelled());

        return "admin/admin";
    }
}

    