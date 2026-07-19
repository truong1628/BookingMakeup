package com.booking.bookingmakeup.controller.admin;

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

import com.booking.bookingmakeup.entity.MakeupService;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.ServiceService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {

    private final ServiceService serviceService;

    public AdminServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    private boolean checkAdmin(HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        return loginUser != null
                && "ADMIN".equals(loginUser.getRole());
    }

    // =============================
    // Danh sách dịch vụ
    // GET /admin/services
    // =============================
    @GetMapping
    public String index(
            HttpSession session,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute(
                "services",
                serviceService.getAllServices());

        return "admin/services";
    }

    // =============================
    // Form thêm
    // GET /admin/services/add
    // =============================
    @GetMapping("/add")
    public String create(
            HttpSession session,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute(
                "service",
                new MakeupService());

        return "admin/service-form";
    }

    // =============================
    // Lưu
    // POST /admin/services
    // =============================
    @PostMapping
    public String store(
            HttpSession session,
            @Valid @ModelAttribute("service") MakeupService service,
            BindingResult result) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "admin/service-form";
        }

        serviceService.save(service);

        return "redirect:/admin/services";
    }

    // =============================
    // Form sửa
    // GET /admin/services/{id}/edit
    // =============================
    @GetMapping("/{id}/edit")
    public String edit(
            HttpSession session,
            @PathVariable Long id,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute(
                "service",
                serviceService.getServiceById(id));

        return "admin/service-form";
    }

    // =============================
    // Cập nhật
    // PUT /admin/services/{id}
    // =============================
    @PutMapping("/{id}")
    public String update(
            HttpSession session,
            @PathVariable Long id,
            @Valid @ModelAttribute("service") MakeupService service,
            BindingResult result) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "admin/service-form";
        }

        service.setId(id);

        serviceService.save(service);

        return "redirect:/admin/services";
    }

    // =============================
    // Xóa
    // DELETE /admin/services/{id}
    // =============================
    @DeleteMapping("/{id}")
    public String delete(
            HttpSession session,
            @PathVariable Long id) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        serviceService.delete(id);

        return "redirect:/admin/services";
    }

}