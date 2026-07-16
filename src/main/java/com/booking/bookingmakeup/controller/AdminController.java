package com.booking.bookingmakeup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.BookingService;
import com.booking.bookingmakeup.service.MakeupArtistService;
import com.booking.bookingmakeup.service.ServiceService;

import jakarta.servlet.http.HttpSession;


@Controller
public class AdminController {

    private final BookingService bookingService;
    private final ServiceService serviceService; 
    private final MakeupArtistService artistService;

    public AdminController(
            BookingService bookingService,
            ServiceService serviceService,
            MakeupArtistService artistService) {

        this.bookingService = bookingService;
        this.serviceService = serviceService;
        this.artistService = artistService;
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

        model.addAttribute("bookings",
                bookingService.getAllBookings());
        model.addAttribute("totalBookings",
        bookingService.countAll());

        model.addAttribute("pendingBookings",
                bookingService.countPending());

        model.addAttribute("confirmedBookings",
                bookingService.countConfirmed());

        model.addAttribute("completedBookings",
                bookingService.countCompleted());

        model.addAttribute("cancelledBookings",
                bookingService.countCancelled());
        return "admin";
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

    

    @PostMapping("/admin/{id}/confirm")
    public String confirmBooking(@PathVariable Long id) {

        bookingService.confirmBooking(id);

        return "redirect:/admin";
    }

   @PostMapping("/admin/{id}/complete")
    public String completeBooking(@PathVariable Long id) {

        bookingService.completeBooking(id);

        return "redirect:/admin";
    }

   @PostMapping("/admin/{id}/cancel")
    public String cancelBookingAdmin(@PathVariable Long id) {

        bookingService.adminCancelBooking(id);

        return "redirect:/admin";
    }
    @GetMapping("/admin/artists")
    public String adminArtists(
            HttpSession session,
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("artists", artistService.getAll());

        return "admin-artists";
    }

    
   
}