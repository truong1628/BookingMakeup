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
import com.booking.bookingmakeup.service.ReviewService;
import com.booking.bookingmakeup.service.ServiceService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/booking")
public class BookingController {
    private final ServiceService serviceService;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    public BookingController(
            ServiceService serviceService,
            BookingService bookingService,
            ReviewService reviewService) {
        this.serviceService = serviceService;
        this.bookingService = bookingService;
        this.reviewService = reviewService;
    }

    // Hiển thị form đặt lịch (ĐÃ BỔ SUNG TRUYỀN loginUser SANG MODEL)
    @GetMapping("/create/{serviceId}")
    public String bookingPage(
            @PathVariable Long serviceId, 
            HttpSession session, 
            Model model) {
        
        // Lấy thông tin user từ session và truyền sang Model cho Navbar nhận biết
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

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

        User loginUser = (User) session.getAttribute("loginUser");

        // Nếu chưa đăng nhập mà nhấn Đặt lịch -> Chuyển về trang login
        if (loginUser == null) {
            return "redirect:/login";
        }

        MakeupService service = serviceService.getServiceById(serviceId);

        // Kiểm tra địa chỉ nếu đặt tại nhà
        if ("HOME".equals(booking.getLocationType())) {

            if (booking.getAddress() == null || booking.getAddress().trim().isEmpty()) {
                model.addAttribute("loginUser", loginUser); // Truyền lại loginUser khi báo lỗi
                model.addAttribute("service", service);
                model.addAttribute("today", LocalDate.now());
                model.addAttribute("timeSlots", generateTimeSlots());
                model.addAttribute("addressError", "Vui lòng nhập địa chỉ.");
                return "user/booking";
            }

            if (booking.getPhone() == null || booking.getPhone().trim().isEmpty()) {
                model.addAttribute("loginUser", loginUser); // Truyền lại loginUser khi báo lỗi
                model.addAttribute("service", service);
                model.addAttribute("today", LocalDate.now());
                model.addAttribute("timeSlots", generateTimeSlots());
                model.addAttribute("phoneError", "Vui lòng nhập số điện thoại.");
                return "user/booking";
            }
        }

        booking.setUser(loginUser);
        booking.setService(service);
        booking.setArtist(null);
        booking.setStatus("PENDING");

        bookingService.save(booking);

        return "redirect:/booking/my";
    }

    // Lịch của tôi
    @GetMapping("/my")
    public String myBookings(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("bookings", bookingService.getBookingsByUser(loginUser));
        model.addAttribute("reviewService", reviewService);

        return "user/my-booking";
    }

    // Hủy lịch
    @DeleteMapping("/{id}")
    public String cancelBooking(@PathVariable Long id, HttpSession session) {
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

        if (!"ADMIN".equals(loginUser.getRole())) {
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