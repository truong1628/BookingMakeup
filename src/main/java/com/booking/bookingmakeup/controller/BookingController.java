package com.booking.bookingmakeup.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.MakeupArtist;
import com.booking.bookingmakeup.entity.MakeupService;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.BookingService;
import com.booking.bookingmakeup.service.MakeupArtistService;
import com.booking.bookingmakeup.service.ServiceService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final ServiceService serviceService;
    private final BookingService bookingService;
    private final MakeupArtistService artistService;

    public BookingController(
            ServiceService serviceService,
            BookingService bookingService,
            MakeupArtistService artistService) {

        this.serviceService = serviceService;
        this.bookingService = bookingService;
        this.artistService = artistService;
    }

    // Hiển thị form đặt lịch
   @GetMapping("/create/{serviceId}")
        public String bookingPage(
                @PathVariable Long serviceId,
                Model model) {

        model.addAttribute("booking", new Booking());

        model.addAttribute("service",
                serviceService.getServiceById(serviceId));

        model.addAttribute("artists",
                artistService.getAll());

        model.addAttribute("today", LocalDate.now()); // thêm dòng này

        return "booking";
        }

    // Lưu lịch đặt
    @PostMapping
    public String saveBooking(

            @Valid @ModelAttribute("booking") Booking booking,
            BindingResult result,

            @RequestParam Long serviceId,
            @RequestParam Long artistId,

            HttpSession session,

            Model model) {

        User loginUser =
                (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        MakeupService service =
                serviceService.getServiceById(serviceId);
        MakeupArtist artist =
                artistService.getById(artistId);
        // kiểm tra validation
        if (artist == null) {

            result.rejectValue(
                    "artist",
                    "",
                    "Makeup Artist không tồn tại.");

            model.addAttribute("service", service);
            model.addAttribute("artists", artistService.getAll());

            return "booking";
        }

        // kiểm tra trùng lịch
        if (bookingService.existsBooking(
                artist,
                booking.getBookingDate(),
                booking.getBookingTime())) {

            result.rejectValue(
                    "bookingTime",
                    "",
                    "Khung giờ này đã có người đặt.");

            model.addAttribute("service", service);
            model.addAttribute("artists", artistService.getAll());

            return "booking";
        }

        booking.setUser(loginUser);
        booking.setService(service);
        booking.setArtist(artist);
        booking.setStatus("Pending");

        bookingService.save(booking);

        return "redirect:/booking/my";
    }

    // Lịch của tôi
    @GetMapping("/my")
    public String myBookings(
            HttpSession session,
            Model model) {

        User loginUser =
                (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "bookings",
                bookingService.getBookingsByUser(loginUser));

        return "MyBooking";
    }

    // Hủy lịch
    @DeleteMapping("/{id}")
    public String cancelBooking(
            @PathVariable Long id,
            HttpSession session) {

        User loginUser =
                (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

    bookingService.cancelBooking(id, loginUser);
        return "redirect:/booking/my";
    }
}