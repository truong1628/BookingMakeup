package com.booking.bookingmakeup.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.BookingService;
import com.booking.bookingmakeup.service.MakeupArtistService;
import com.booking.bookingmakeup.service.ReviewService;
import com.booking.bookingmakeup.service.ServiceService;
import com.booking.bookingmakeup.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {

    private final UserService userService;
    private final ServiceService serviceService;
    private final MakeupArtistService artistService;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    public AdminController(
            UserService userService,
            ServiceService serviceService,
            MakeupArtistService artistService,
            BookingService bookingService,
            ReviewService reviewService) {

        this.userService = userService;
        this.serviceService = serviceService;
        this.artistService = artistService;
        this.bookingService = bookingService;
        this.reviewService = reviewService;
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

        // 1. Các con số thống kê tổng quan
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalServices", serviceService.countServices());
        model.addAttribute("totalArtists", artistService.countArtists());
        model.addAttribute("totalReviews", reviewService.countReviews());

        // 2. Thống kê số lượng booking theo trạng thái
        model.addAttribute("totalBookings", bookingService.countAll());
        model.addAttribute("pendingBookings", bookingService.countPending());
        model.addAttribute("confirmedBookings", bookingService.countConfirmed());
        model.addAttribute("completedBookings", bookingService.countCompleted());
        model.addAttribute("cancelledBookings", bookingService.countCancelled());

        // 3. Thống kê Doanh thu (Hôm nay, Tháng này, Năm nay, Tổng)
        model.addAttribute("todayRevenue", bookingService.getTodayRevenue());
        model.addAttribute("monthRevenue", bookingService.getMonthRevenue());
        model.addAttribute("yearRevenue", bookingService.getYearRevenue());
        model.addAttribute("totalRevenue", bookingService.getTotalRevenue());

        // 4. Danh sách Booking & Doanh thu theo Artist
        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("artistRevenue", bookingService.getArtistRevenue());

        return "admin/admin";
    }
}