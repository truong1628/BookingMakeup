package com.booking.bookingmakeup.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.booking.bookingmakeup.entity.MakeupService;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.ReviewService;
import com.booking.bookingmakeup.service.ServiceService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/service")
public class UserServiceController {

    private final ServiceService serviceService;
    private final ReviewService reviewService;

    public UserServiceController(ServiceService serviceService, ReviewService reviewService) {
        this.serviceService = serviceService;
        this.reviewService = reviewService;
    }

    // Xem chi tiết dịch vụ
    // GET /service/{id}
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        MakeupService service = serviceService.getServiceById(id);
        model.addAttribute("service", service);
        model.addAttribute("reviews", reviewService.getReviewsByService(service));
        model.addAttribute("avgRating", reviewService.getAverageRating(service));

        return "user/detail";
    }

    // Danh sách dịch vụ
    @GetMapping
    public String services(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("services", serviceService.getAllServices());

        return "services";
    }
}