package com.booking.bookingmakeup.controller.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
import org.springframework.web.multipart.MultipartFile;

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

    // Danh sách dịch vụ
    @GetMapping
    public String services(
            HttpSession session,
            Model model) {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute(
                "services",
                serviceService.getActiveServices());

        return "admin/services";
    }

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

    @PostMapping
    public String addService(
            HttpSession session,
            @Valid @ModelAttribute("service") MakeupService service,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile)
            throws IOException {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        if (imageFile.isEmpty()) {
            result.rejectValue(
                "image",
                "",
                "Ảnh không được để trống");
        }

        if (result.hasErrors()) {
            return "admin/service-form";
        }

        if (!imageFile.isEmpty()) {

            String fileName = System.currentTimeMillis()
                    + "_" + imageFile.getOriginalFilename();

            Path uploadPath = Paths.get("uploads/services");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(
                    imageFile.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);

            service.setImage("services/" + fileName);
        }

        serviceService.save(service);

        return "redirect:/admin/services";

        
    }
   

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


    @PutMapping("/{id}")
    public String update(
            HttpSession session,
            @PathVariable Long id,
            @Valid @ModelAttribute("service") MakeupService service,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile)
            throws IOException {

        if (!checkAdmin(session)) {
            return "redirect:/login";
        }

        if (result.hasErrors()) {
            return "admin/service-form";
        }

        service.setId(id);
        if (!imageFile.isEmpty()) {

            String fileName = System.currentTimeMillis()
                    + "_" + imageFile.getOriginalFilename();

            Path uploadPath = Paths.get("uploads/services");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(
                    imageFile.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);

            service.setImage("services/" + fileName);

        } else {

            MakeupService oldService = serviceService.getServiceById(id);

            service.setImage(oldService.getImage());
        }
        serviceService.save(service);

        return "redirect:/admin/services";
    }


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