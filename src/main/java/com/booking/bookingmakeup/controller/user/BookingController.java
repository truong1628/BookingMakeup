package com.booking.bookingmakeup.controller.user;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.ResponseBody;

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
        @PathVariable Long serviceId, Model model) {
        model.addAttribute("booking", new Booking());
        model.addAttribute("service", serviceService.getServiceById(serviceId));
        model.addAttribute("artists", artistService.getAll());
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
        @RequestParam Long artistId,
        HttpSession session,
        Model model) {
        User loginUser =(User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        MakeupService service = serviceService.getServiceById(serviceId);
        MakeupArtist artist = artistService.getById(artistId);
        // kiểm tra validation
        if (artist == null) {
                result.rejectValue("artist","","Makeup Artist không tồn tại.");
                model.addAttribute("service", service);
                model.addAttribute("artists", artistService.getAll());
                model.addAttribute("today", LocalDate.now());
                return "/user/booking";
        }

        // kiểm tra trùng lịch
        if (bookingService.existsBooking(
                artist,
                booking.getBookingDate(),
                booking.getBookingTime())) {
                result.rejectValue("bookingTime","","Khung giờ này đã có người đặt.");
                model.addAttribute("service", service);
                model.addAttribute("artists", artistService.getAll());
                model.addAttribute("today", LocalDate.now());
                return "user/booking";
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

        bookingService.completeBooking(id);

        return "redirect:/admin";
    }

    @GetMapping("/booked-times")
    @ResponseBody
   public List<LocalTime> getBookedTimes(
            @RequestParam Long artistId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        MakeupArtist artist = artistService.getById(artistId);
        if (artist == null) {
            return List.of();
        }
        return bookingService.getBookedTimes(artist, date);
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