package com.booking.bookingmakeup.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.BookingService;
import com.booking.bookingmakeup.service.MakeupArtistService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminBookingController {

    private final BookingService bookingService;
    private final MakeupArtistService artistService;

    public AdminBookingController(BookingService bookingService, MakeupArtistService artistService) {
        this.bookingService = bookingService;
        this.artistService = artistService;
    }

    @PutMapping("/admin/bookings/{id}/confirm")
    public String confirmBooking(@PathVariable Long id) {
        bookingService.confirmBooking(id);
        return "redirect:/admin";
    }

    @PutMapping("/admin/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.adminCancelBooking(id);
        return "redirect:/admin";
    }

    @PutMapping("/admin/bookings/{id}/complete")
    public String completeBooking(@PathVariable Long id) {
        bookingService.completeBooking(id); 
        return "redirect:/admin";
    }

    @GetMapping("/admin/bookings/{id}/assign")
    public String assignArtistPage(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/";
        }

        Booking booking = bookingService.getBookingById(id);
        if (booking == null) {
            return "redirect:/admin";
        }

        model.addAttribute("booking", booking);
        model.addAttribute("artists", artistService.getAvailableArtists(
                booking.getBookingDate(),
                booking.getBookingTime()));
                
        return "admin/assign-artist";
    }

    @PostMapping("/admin/bookings/{id}/assign")
    public String assignArtist(
            @PathVariable Long id,
            @RequestParam Long artistId,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/";
        }

        bookingService.assignArtist(id, artistId);
        return "redirect:/admin";
    }

    // 🟢 ADMIN CHẤP NHẬN YÊU CẦU HỦY CỦA ARTIST
    @PostMapping("/admin/bookings/{id}/approve-cancel")
    public String approveCancel(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/login";
        }

        bookingService.approveCancelBooking(id); // Gọi hàm từ Service
        return "redirect:/admin";
    }

    // 🟢 ADMIN TỪ CHỐI YÊU CẦU HỦY (KHÔI PHỤC LẠI TRẠNG THÁI CONFIRMED)
    @PostMapping("/admin/bookings/{id}/reject-cancel")
    public String rejectCancel(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/login";
        }

        bookingService.rejectCancelBooking(id); // Gọi hàm từ Service
        return "redirect:/admin";
    }
}