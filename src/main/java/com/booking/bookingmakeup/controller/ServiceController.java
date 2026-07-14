package com.booking.bookingmakeup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.booking.bookingmakeup.entity.MakeupService;
import com.booking.bookingmakeup.service.ServiceService;

@Controller
@RequestMapping("/service")
public class ServiceController {

    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        model.addAttribute("service",
                serviceService.getServiceById(id));

        return "detail";
    }
    @GetMapping("/services")
    public String services(Model model) {

        model.addAttribute(
                "services",
                serviceService.getAllServices());

        return "services";
    }

    @GetMapping("/service/add")
    public String addServicePage(Model model) {

        model.addAttribute(
                "service",
                new MakeupService());

        return "add-service";
    }

    @GetMapping("/edit/{id}")
    public String editService(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "service",
                serviceService.getServiceById(id));

        return "edit-service";
    }

    @PostMapping("/update")
    public String updateService(
            @ModelAttribute MakeupService service) {

        System.out.println("ID = " + service.getId());
        System.out.println("Tên = " + service.getServiceName());

        serviceService.save(service);

        return "redirect:/admin/services";
    }

    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable Long id) {

        serviceService.delete(id);

        return "redirect:/admin/services";
    }
}