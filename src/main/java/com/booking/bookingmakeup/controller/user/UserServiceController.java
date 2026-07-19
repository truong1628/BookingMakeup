package com.booking.bookingmakeup.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.booking.bookingmakeup.service.ServiceService;


@Controller
@RequestMapping("/service")
public class UserServiceController {

    private final ServiceService serviceService;

    public UserServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    // ==========================
    // Xem chi tiết dịch vụ
    // GET /service/{id}
    // ==========================
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        model.addAttribute(
                "service",
                serviceService.getServiceById(id));

        return "/user/detail";
    }

    // ==========================
    // Danh sách dịch vụ
    // GET /service/services
    // ==========================
    @GetMapping
    public String services(Model model) {

        model.addAttribute(
                "services",
                serviceService.getAllServices());

        return "services";
    }


}