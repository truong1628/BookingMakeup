package com.booking.bookingmakeup.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.booking.bookingmakeup.entity.Gallery;
import com.booking.bookingmakeup.repository.GalleryRepository;

@RestController
@RequestMapping("/api/gallery")
public class PublicGalleryController {

    private final GalleryRepository galleryRepository;

    // Sử dụng Constructor Injection thay cho @Autowired field
    public PublicGalleryController(GalleryRepository galleryRepository) {
        this.galleryRepository = galleryRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> showPublicGallery(
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        List<Gallery> galleries;

        if (keyword != null && !keyword.trim().isEmpty()) {
            galleries = galleryRepository.findByTitleContainingIgnoreCaseOrderByIdDesc(keyword.trim());
        } else {
            galleries = galleryRepository.findAllByOrderByIdDesc();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("galleries", galleries);
        response.put("keyword", keyword != null ? keyword.trim() : "");
        response.put("totalItems", galleries.size());

        return ResponseEntity.ok(response);
    }
}