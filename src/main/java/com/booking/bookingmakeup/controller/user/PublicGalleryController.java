package com.booking.bookingmakeup.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.booking.bookingmakeup.entity.Gallery;
import com.booking.bookingmakeup.repository.GalleryRepository;

@Controller
public class PublicGalleryController {

    @Autowired
    private GalleryRepository galleryRepository;

    @GetMapping("/gallery")
    public String showPublicGallery(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Gallery> galleries;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            galleries = galleryRepository.findByTitleContainingIgnoreCaseOrderByIdDesc(keyword.trim());
        } else {
            galleries = galleryRepository.findAllByOrderByIdDesc();
        }

        model.addAttribute("galleries", galleries);
        model.addAttribute("keyword", keyword);
        
        return "public-gallery"; 
    }
}