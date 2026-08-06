package com.booking.bookingmakeup.controller.artist;

import java.time.LocalDate;
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
import com.booking.bookingmakeup.service.ReviewService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/artist")
public class ArtistController {

    private final MakeupArtistService artistService;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    public ArtistController(MakeupArtistService artistService,
                            BookingService bookingService,
                            ReviewService reviewService) {
        this.artistService = artistService;
        this.bookingService = bookingService;
        this.reviewService = reviewService;
    }

    // Lấy object Artist/User từ session gọn gàng
    private Object getCurrentArtistFromSession(HttpSession session) {
        Object artist = session.getAttribute("loginArtist");
        if (artist == null) artist = session.getAttribute("artist");
        if (artist == null) artist = session.getAttribute("loginUser");
        return artist;
    }

    // Lấy ID an toàn bất kể đăng nhập từ nguồn nào
    private Long getArtistIdFromSession(HttpSession session) {
        Object loginArtist = session.getAttribute("loginArtist");
        if (loginArtist != null) {
            try {
                return (Long) loginArtist.getClass().getMethod("getId").invoke(loginArtist);
            } catch (Exception ignored) {
            }
        }

        // Quét dự phòng tất cả attribute khác trong session
        Enumeration<String> keys = session.getAttributeNames();
        while (keys.hasMoreElements()) {
            Object obj = session.getAttribute(keys.nextElement());
            if (obj != null) {
                try {
                    Object id = obj.getClass().getMethod("getId").invoke(obj);
                    if (id instanceof Long) return (Long) id;
                } catch (Exception ignored) {
                }
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
        return "redirect:/artist/dashboard";
    }

    // Trang Dashboard dành cho Artist
    @GetMapping({"", "/dashboard"})
    public String dashboard(HttpSession session, Model model) {
        Long artistId = getArtistIdFromSession(session);

        if (artistId == null) {
            return "redirect:/artist/login";
        }

        Object currentArtist = getCurrentArtistFromSession(session);
        List<Booking> bookings = bookingService.getBookingsByArtist(artistId);

        // 1. Thống kê số lượng & doanh thu
        long totalBookings = (bookings != null) ? bookings.size() : 0;
        long completedBookings = 0;
        long inProgressBookings = 0;
        double monthRevenue = 0.0;

        LocalDate now = LocalDate.now();

        if (bookings != null) {
            for (Booking booking : bookings) {
                String status = booking.getStatus();

                if ("COMPLETED".equalsIgnoreCase(status)) {
                    completedBookings++;

                    if (booking.getBookingDate() != null) {
                        LocalDate bookingDate = booking.getBookingDate();
                        if (bookingDate.getMonthValue() == now.getMonthValue() && bookingDate.getYear() == now.getYear()) {
                            double price = (booking.getService() != null && booking.getService().getPrice() != null)
                                    ? booking.getService().getPrice() : 0.0;
                            monthRevenue += price;
                        }
                    }
                } else if ("CONFIRMED".equalsIgnoreCase(status) || "IN_PROGRESS".equalsIgnoreCase(status)) {
                    inProgressBookings++;
                }
            }
        }

        // 2. Điểm đánh giá trung bình
        Double averageRating = reviewService.getAverageRatingByArtist(artistId);

        model.addAttribute("artist", currentArtist);
        model.addAttribute("totalBookings", totalBookings);
        model.addAttribute("completedBookings", completedBookings);
        model.addAttribute("inProgressBookings", inProgressBookings);
        model.addAttribute("monthRevenue", monthRevenue);
        model.addAttribute("averageRating", averageRating != null ? averageRating : 0.0);

        // Lấy tối đa 5 đơn gần nhất
        List<Booking> recentBookings = (bookings != null && bookings.size() > 5)
                ? bookings.subList(0, 5)
                : bookings;
        model.addAttribute("recentBookings", recentBookings);

        return "artist/dashboard";
    }

    // Trang Danh sách lịch làm việc
    @GetMapping("/bookings")
    public String showArtistBookings(HttpSession session, Model model) {
        Long artistId = getArtistIdFromSession(session);

        if (artistId == null) {
            return "redirect:/artist/login";
        }

        Object currentArtist = getCurrentArtistFromSession(session);
        List<Booking> bookings = bookingService.getBookingsByArtist(artistId);

        if (bookings != null) {
            for (Booking booking : bookings) {
                double totalPrice = (booking.getService() != null && booking.getService().getPrice() != null)
                        ? booking.getService().getPrice() : 0.0;
                double deposit = (booking.getDepositAmount() != null) ? booking.getDepositAmount() : 0.0;

                if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
                    booking.setRemainingAmount(Math.max(0.0, totalPrice - deposit));
                } else {
                    booking.setRemainingAmount(totalPrice);
                }
            }
        }

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