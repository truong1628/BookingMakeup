package com.booking.bookingmakeup.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.booking.bookingmakeup.entity.MakeupArtist;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.service.MakeupArtistService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/artists")
public class AdminArtistController {

    private final MakeupArtistService artistService;

    public AdminArtistController(MakeupArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public String adminArtists(
            HttpSession session,
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("artists", artistService.getAll());

        return "admin/artists";
    }

    @GetMapping("/add")
    public String addArtistPage(Model model) {

        model.addAttribute("artist", new MakeupArtist());

        return "admin/artist-form";
    }

    @PostMapping
    public String saveArtist(
            @ModelAttribute MakeupArtist artist) {

        artistService.save(artist);

        return "redirect:/admin/artists";
    }

    @GetMapping("/{id}/edit")
    public String editArtist(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/";
        }

        model.addAttribute(
                "artist",
                artistService.getById(id));

        return "admin/artist-form";
    }

    @PutMapping("/{id}")
    public String updateArtist(
            @PathVariable Long id,
            @ModelAttribute MakeupArtist artist) {

        artist.setId(id);

        artistService.save(artist);

        return "redirect:/admin/artists";
    }

    @DeleteMapping("/{id}")
    public String deleteArtist(
            @PathVariable Long id,
            HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");

        if (loginUser == null) {
            return "redirect:/login";
        }

        if (!"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/";
        }

        artistService.delete(id);

        return "redirect:/admin/artists";
    }

}