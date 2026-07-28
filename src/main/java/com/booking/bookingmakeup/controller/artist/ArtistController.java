package com.booking.bookingmakeup.controller.artist;

import java.util.Enumeration;
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
@RequestMapping("/artist")
public class ArtistController {

    private final MakeupArtistService artistService;
    private final BookingService bookingService;

    public ArtistController(MakeupArtistService artistService, BookingService bookingService) {
        this.artistService = artistService;
        this.bookingService = bookingService;
    }

    // Hàm lấy ID an toàn bất kể đăng nhập từ AuthController hay ArtistController
    private Long getArtistIdFromSession(HttpSession session) {
        Object loginArtist = session.getAttribute("loginArtist");
        if (loginArtist != null) {
            try {
                return (Long) loginArtist.getClass().getMethod("getId").invoke(loginArtist);
            } catch (Exception ignored) {}
        }
        
        // Quét dự phòng tất cả attribute khác
        Enumeration<String> keys = session.getAttributeNames();
        while (keys.hasMoreElements()) {
            Object obj = session.getAttribute(keys.nextElement());
            if (obj != null) {
                try {
                    Object id = obj.getClass().getMethod("getId").invoke(obj);
                    if (id instanceof Long) return (Long) id;
                } catch (Exception ignored) {}
            }
        }
        return null;
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

    // 👈 Đã thêm "/schedule" vào danh sách Mapping để bao quát toàn bộ URL
    @GetMapping({"", "/dashboard", "/bookings", "/schedule"})
    public String showArtistBookings(HttpSession session, Model model) {
        Long artistId = getArtistIdFromSession(session);

        if (artistId == null) {
            return "redirect:/artist/login";
        }

        Object currentArtist = session.getAttribute("loginArtist");
        if (currentArtist == null) currentArtist = session.getAttribute("artist");
        if (currentArtist == null) currentArtist = session.getAttribute("loginUser");

        List<Booking> bookings = bookingService.getBookingsByArtist(artistId);

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