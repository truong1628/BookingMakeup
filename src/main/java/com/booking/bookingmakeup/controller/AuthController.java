package com.booking.bookingmakeup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.UserService;

import jakarta.servlet.http.HttpSession;


@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute("user", new User());

        return "register";
    }

    @GetMapping("/login")
    public String loginPage() {

        return "login";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute User user,
            RedirectAttributes redirectAttributes) {

        if (userService.existsByEmail(user.getEmail())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Email đã tồn tại!");

            return "redirect:/register";
        }

        userService.save(user);

        redirectAttributes.addFlashAttribute(
                "success",
                "Đăng ký thành công!");

        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        User user = userService.login(email, password);

        if (user == null) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Sai email hoặc mật khẩu!");
            return "redirect:/login";
        }
        session.setAttribute("loginUser", user);


        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }
    
}