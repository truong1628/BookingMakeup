package com.booking.bookingmakeup.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.Review;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.BookingService;
import com.booking.bookingmakeup.service.ReviewService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
@Controller
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;
    private final BookingService bookingService;

    public ReviewController(
            ReviewService reviewService,
            BookingService bookingService) {

        this.reviewService = reviewService;
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public String createReview(
            @PathVariable Long bookingId,
            HttpSession session,
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        Booking booking = bookingService.getBookingById(bookingId);

        if (booking == null) {
            return "redirect:/booking/my";
        }

        if (!booking.getUser().getId().equals(loginUser.getId())) {
            return "redirect:/booking/my";
        }

        if (!"COMPLETED".equals(booking.getStatus())) {
            return "redirect:/booking/my";
        }

        if (reviewService.hasReview(booking)) {
            return "redirect:/booking/my";
        }

        model.addAttribute("booking", booking);
        model.addAttribute("review", new Review());

        return "user/review-form";
    }
    @PostMapping
    public String saveReview(
            @Valid @ModelAttribute("review") Review review,
            BindingResult result,
            @RequestParam("bookingId") Long bookingId,
            HttpSession session,
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        Booking booking = bookingService.getBookingById(bookingId);

        if (booking == null) {
            return "redirect:/booking/my";
        }

        if (result.hasErrors()) {

            model.addAttribute("booking", booking);

            return "user/review-form";
        }

        review.setBooking(booking);
        review.setUser(loginUser);
        review.setService(booking.getService());


        reviewService.save(review);

        return "redirect:/booking/my";
    }
}