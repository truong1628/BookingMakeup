package com.booking.bookingmakeup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    // ==========================
    // Xem chi tiết dịch vụ
    // GET /service/{id}
    // ==========================
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        model.addAttribute(
                "service",
                serviceService.getServiceById(id));

        return "detail";
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

    // ==========================
    // Form thêm dịch vụ
    // GET /service/add
    // ==========================
    @GetMapping("/add")
    public String addServicePage(Model model) {

        model.addAttribute(
                "service",
                new MakeupService());

        return "add-service";
    }

    // ==========================
    // Lưu dịch vụ mới
    // POST /service
    // ==========================
    @PostMapping
    public String saveService(
            @ModelAttribute MakeupService service) {

        serviceService.save(service);

        return "redirect:/admin/services";
    }

    // ==========================
    // Form sửa
    // GET /service/edit/{id}
    // ==========================
    @GetMapping("/edit/{id}")
    public String editService(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "service",
                serviceService.getServiceById(id));

        return "edit-service";
    }

    // ==========================
    // Cập nhật
    // PUT /service/{id}
    // ==========================
    @PutMapping("/{id}")
    public String updateService(
            @PathVariable Long id,
            @ModelAttribute MakeupService service) {

        service.setId(id);

        serviceService.save(service);

        return "redirect:/admin/services";
    }

    // ==========================
    // Xóa
    // DELETE /service/{id}
    // ==========================
    @DeleteMapping("/{id}")
    public String deleteService(
            @PathVariable Long id) {

        serviceService.delete(id);

        return "redirect:/admin/services";
    }

}