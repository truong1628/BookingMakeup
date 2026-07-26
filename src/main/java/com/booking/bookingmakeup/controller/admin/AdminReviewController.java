package com.booking.bookingmakeup.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.ReviewService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {

    private final ReviewService reviewService;

    public AdminReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public String index(HttpSession session,
                        Model model) {

        User loginUser =
                (User) session.getAttribute("loginUser");

        if (loginUser == null ||
            !"ADMIN".equals(loginUser.getRole())) {

            return "redirect:/login";
        }

        model.addAttribute(
                "reviews",
                reviewService.getAllReviews());

        return "admin/reviews";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id,
                         HttpSession session) {

        User loginUser =
                (User) session.getAttribute("loginUser");

        if (loginUser == null ||
            !"ADMIN".equals(loginUser.getRole())) {

            return "redirect:/login";
        }

        reviewService.delete(id);

        return "redirect:/admin/reviews";
    }

}