package com.booking.bookingmakeup.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.repository.BookingRepository;
import com.booking.bookingmakeup.service.BookingService;
import com.booking.bookingmakeup.service.MakeupArtistService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminBookingController {

    private final BookingService bookingService;
    private final MakeupArtistService artistService;
    private final BookingRepository bookingRepository;

    public AdminBookingController(BookingService bookingService, 
                                  MakeupArtistService artistService, 
                                  BookingRepository bookingRepository) {
        this.bookingService = bookingService;
        this.artistService = artistService;
        this.bookingRepository = bookingRepository;
    }

    // 1. 🟢 HIỂN THỊ TRANG QUẢN LÝ BOOKING (CÓ BỘ LỌC STATUS)
    @GetMapping("/admin/bookings")
    public String showBookingsPage(
            @RequestParam(value = "status", required = false) String status,
            HttpSession session, 
            Model model) {
        
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/";
        }

        List<Booking> bookings;

        // Nếu có truyền status và status không rỗng thì lọc, ngược lại lấy tất cả
        if (status != null && !status.trim().isEmpty()) {
            bookings = bookingRepository.findByStatus(status);
        } else {
            bookings = bookingService.getAllBookings();
        }

        model.addAttribute("bookings", bookings);
        model.addAttribute("selectedStatus", status); // Gửi biến này về HTML để active nút lọc tương ứng

        return "admin/bookings"; // Trả về file admin/bookings.html (hoặc /admin/bookings tùy cấu hình ViewResolver)
    }

    // 2. 🟢 CHẤP NHẬN BOOKING
    @PutMapping("/admin/bookings/{id}/confirm")
    public String confirmBooking(@PathVariable Long id) {
        bookingService.confirmBooking(id);
        return "redirect:/admin/bookings";
    }

    // 3. 🟢 HỦY BOOKING
    @PutMapping("/admin/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id) {
        bookingService.adminCancelBooking(id);
        return "redirect:/admin/bookings";
    }

    // 4. 🟢 HOÀN THÀNH BOOKING
    @PutMapping("/admin/bookings/{id}/complete")
    public String completeBooking(@PathVariable Long id) {
        bookingService.completeBooking(id); 
        return "redirect:/admin/bookings";
    }

    // 5. 🟢 PHÂN CÔNG ARTIST (GIAO DIỆN)
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
            return "redirect:/admin/bookings";
        }

        model.addAttribute("booking", booking);
        model.addAttribute("artists", artistService.getAvailableArtists(
                booking.getBookingDate(),
                booking.getBookingTime()));
                
        return "admin/assign-artist";
    }

    // 6. 🟢 XỬ LÝ PHÂN CÔNG ARTIST
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
        return "redirect:/admin/bookings";
    }

    // 7. 🟢 ADMIN CHẤP NHẬN YÊU CẦU HỦY CỦA ARTIST
    @PostMapping("/admin/bookings/{id}/approve-cancel")
    public String approveCancel(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/login";
        }

        bookingService.approveCancelBooking(id);
        return "redirect:/admin/bookings";
    }

    // 8. 🟢 ADMIN TỪ CHỐI YÊU CẦU HỦY
    @PostMapping("/admin/bookings/{id}/reject-cancel")
    public String rejectCancel(@PathVariable Long id, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/login";
        }

        bookingService.rejectCancelBooking(id);
        return "redirect:/admin/bookings";
    }
}