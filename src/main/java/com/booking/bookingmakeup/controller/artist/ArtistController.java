package com.booking.bookingmakeup.controller.artist;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.MakeupArtist;
import com.booking.bookingmakeup.service.BookingService;
import com.booking.bookingmakeup.service.MakeupArtistService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/artist") // 👈 1. Đặt prefix /artist ở đây cho gọn
public class ArtistController {

    private final MakeupArtistService artistService;
    private final BookingService bookingService;

    public ArtistController(MakeupArtistService artistService, BookingService bookingService) {
        this.artistService = artistService;
        this.bookingService = bookingService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "artist/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        MakeupArtist artist = artistService.login(email, password);

        if (artist == null) {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
            return "artist/login";
        }

        session.setAttribute("loginArtist", artist);
        return "redirect:/artist/bookings";
    }

    // 👈 2. Tất cả đường dẫn /artist, /artist/dashboard, /artist/bookings gom về 1 hàm duy nhất
    @GetMapping({"", "/dashboard", "/bookings"})
    public String showArtistBookings(HttpSession session, Model model) {
        MakeupArtist currentArtist = (MakeupArtist) session.getAttribute("loginArtist");

        if (currentArtist == null) {
            return "redirect:/artist/login";
        }

        System.out.println("==================================================");
        System.out.println(">>> ĐANG LẤY LỊCH CHO ARTIST ID = " + currentArtist.getId());

        List<Booking> bookings = bookingService.getBookingsByArtist(currentArtist.getId());
        
        System.out.println(">>> SỐ LƯỢNG BOOKING TÌM THẤY = " + (bookings != null ? bookings.size() : 0));
        System.out.println("==================================================");

        // Truyền đầy đủ dữ liệu sang Thymeleaf
        model.addAttribute("bookings", bookings);
        model.addAttribute("artist", currentArtist); 

        return "artist/bookings";
    }

    @PutMapping("/bookings/{id}/start")
    public String startBooking(@PathVariable Long id) {
        bookingService.startBooking(id);
        return "redirect:/artist/bookings";
    }

    @PutMapping("/bookings/{id}/complete")
    public String completeBooking(@PathVariable Long id) {
        bookingService.finishBooking(id);
        return "redirect:/artist/bookings";
    }
    @PostMapping("/bookings/{id}/request-cancel")
    public String requestCancel(
            @PathVariable Long id,
            @RequestParam("reason") String reason) {

        bookingService.requestCancelBooking(id, reason);
        return "redirect:/artist/bookings";
    }
}