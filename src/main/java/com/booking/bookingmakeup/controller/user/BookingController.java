package com.booking.bookingmakeup.controller.user;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.MakeupService;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.BookingService;
import com.booking.bookingmakeup.service.ServiceService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/booking")
public class BookingController {
    private final ServiceService serviceService;
    private final BookingService bookingService;
    public BookingController(
            ServiceService serviceService,
            BookingService bookingService) {
        this.serviceService = serviceService;
        this.bookingService = bookingService;
    }

    // Hiển thị form đặt lịch
@GetMapping("/create/{serviceId}")
public String bookingPage(
        @PathVariable Long serviceId, Model model) {
        model.addAttribute("booking", new Booking());
        model.addAttribute("service", serviceService.getServiceById(serviceId));
        model.addAttribute("today", LocalDate.now()); 
        model.addAttribute("timeSlots", generateTimeSlots());
        return "user/booking";
}

    // Lưu lịch đặt
    @PostMapping
    public String saveBooking(
        @Valid @ModelAttribute("booking") Booking booking,
        BindingResult result,
        @RequestParam Long serviceId,
        HttpSession session,
        Model model) {
        User loginUser =(User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        MakeupService service = serviceService.getServiceById(serviceId);
        
        booking.setUser(loginUser);
        booking.setService(service);
        booking.setArtist(null);
        booking.setStatus("PENDING");
        bookingService.save(booking);
        return "redirect:/booking/my";
    }

    // Lịch của tôi
    @GetMapping("/my")
    public String myBookings(HttpSession session,Model model) {
        User loginUser =(User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("bookings",bookingService.getBookingsByUser(loginUser));
        return "user/my-booking";
    }

    // Hủy lịch
    @DeleteMapping("/{id}")
    public String cancelBooking(@PathVariable Long id,HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        bookingService.cancelBooking(id, loginUser);
        return "redirect:/booking/my";
    }
    @PutMapping("/admin/{id}/complete")
    public String completeBooking(
            @PathVariable Long id,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!loginUser.getRole().equals("ADMIN")) {
            return "redirect:/";
        }

        bookingService.finishBooking(id);
        return "redirect:/admin";
    }

    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> times = new ArrayList<>();
        LocalTime start = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(20, 0);
        while (!start.isAfter(end)) {
            times.add(start);
            start = start.plusMinutes(30);
        }
        return times;
    }

}