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

    @GetMapping("/create/{serviceId}")
    public String bookingPage(
            @PathVariable Long serviceId, 
            HttpSession session, 
            Model model) {
        
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

        if (loginUser == null) {
            return "redirect:/login";
        }

        MakeupService service = serviceService.getServiceById(serviceId);

        // Validation kiểm tra thông tin địa chỉ và SĐT khi đặt tại nhà
        if ("HOME".equals(booking.getLocationType())) {
            boolean hasError = false;

            if (booking.getAddress() == null || booking.getAddress().trim().isEmpty()) {
                model.addAttribute("addressError", "Vui lòng nhập địa chỉ.");
                hasError = true;
            }

            if (booking.getPhone() == null || booking.getPhone().trim().isEmpty()) {
                model.addAttribute("phoneError", "Vui lòng nhập số điện thoại.");
                hasError = true;
            }

            if (hasError) {
                model.addAttribute("loginUser", loginUser); 
                model.addAttribute("service", service);
                model.addAttribute("today", LocalDate.now());
                model.addAttribute("timeSlots", generateTimeSlots());
                return "user/booking";
            }
        }

        // Gán thông tin cơ bản
        booking.setUser(loginUser);
        booking.setService(service);
        booking.setArtist(null);
        booking.setStatus("PENDING");

        // Tính toán tổng giá tiền (Giá dịch vụ + Phụ phí di chuyển)
        double travelFee = (booking.getTravelFee() != null) ? booking.getTravelFee() : 0.0;
        double totalPrice = service.getPrice() + travelFee;
        booking.setTotalPrice(totalPrice);

        // --- XỬ LÝ PHÂN LOẠI THANH TOÁN ---
        
        // Trường hợp 1: Makeup tại Studio -> Không cần cọc, thanh toán sau tại cửa hàng
        if ("STUDIO".equals(booking.getLocationType())) {
            booking.setPaymentType("PAY_LATER");
            booking.setPaymentStatus("UNPAID");
            booking.setDepositAmount(0.0);

            bookingService.save(booking);
            return "redirect:/booking/my";
        } 
        
        // Trường hợp 2: Makeup tại nhà -> Bắt buộc chọn Cọc 30% hoặc Thanh toán 100%
        if ("HOME".equals(booking.getLocationType())) {
            String payType = booking.getPaymentType();
            
            // Nếu người dùng chưa chọn, mặc định chọn cọc 30%
            if (payType == null || payType.isEmpty()) {
                payType = "DEPOSIT";
                booking.setPaymentType(payType);
            }

            if ("DEPOSIT".equals(payType)) {
                booking.setDepositAmount(totalPrice * 0.3); // Cọc 30% tổng đơn
            } else if ("FULL".equals(payType)) {
                booking.setDepositAmount(totalPrice);       // Thanh toán 100%
            }

            booking.setPaymentStatus("UNPAID");
            bookingService.save(booking);

            // Chuyển sang màn hình thanh toán chuyển khoản QR
            return "redirect:/payment/" + booking.getId();
        }

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