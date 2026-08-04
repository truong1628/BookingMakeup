package com.booking.bookingmakeup.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.BookingService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final BookingService bookingService;

    public PaymentController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Hiển thị trang chuyển khoản / thanh toán
    @GetMapping("/{bookingId}")
    public String paymentPage(
            @PathVariable Long bookingId, 
            HttpSession session, 
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Booking booking = bookingService.getBookingById(bookingId);

        // Kiểm tra xem booking có tồn tại và có đúng là của user đang đăng nhập không
        if (booking == null || !booking.getUser().getId().equals(loginUser.getId())) {
            return "redirect:/booking/my";
        }

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("booking", booking);

        return "user/payment";
    }

    // Xử lý khi khách hàng bấm nút "Tôi đã chuyển khoản thành công"
    @PostMapping("/process")
    public String processPayment(
            @RequestParam("bookingId") Long bookingId,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Booking booking = bookingService.getBookingById(bookingId);
        if (booking != null && booking.getUser().getId().equals(loginUser.getId())) {
            // Chuyển trạng thái thanh toán thành Chờ Admin/Artist xác nhận đã nhận tiền
            bookingService.updatePaymentStatus(bookingId, "PENDING_APPROVAL");
        }

        return "redirect:/booking/my";
    }
}