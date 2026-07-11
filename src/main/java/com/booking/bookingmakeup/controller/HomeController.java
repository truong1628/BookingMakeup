package com.booking.bookingmakeup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.booking.bookingmakeup.service.ServiceService;

@Controller
public class HomeController {

    private final ServiceService serviceService;

    public HomeController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("services",
                serviceService.getAllServices());

        return "index";
    }
}