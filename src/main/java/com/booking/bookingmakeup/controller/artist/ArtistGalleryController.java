package com.booking.bookingmakeup.controller.artist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.booking.bookingmakeup.entity.Gallery;
import com.booking.bookingmakeup.entity.MakeupArtist;
import com.booking.bookingmakeup.repository.GalleryRepository;
import com.booking.bookingmakeup.repository.MakeupArtistRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/artist/gallery-manage")
public class ArtistGalleryController {

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private MakeupArtistRepository makeupArtistRepository; // Đã đổi sang MakeupArtistRepository

    private final String UPLOAD_DIR = Paths.get("uploads").toAbsolutePath().toString();

    // Hàm lấy ID linh hoạt từ Object trong Session
    private Long getArtistIdFromSession(HttpSession session) {
        Enumeration<String> keys = session.getAttributeNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            Object obj = session.getAttribute(key);
            if (obj != null) {
                try {
                    Object id = obj.getClass().getMethod("getId").invoke(obj);
                    if (id instanceof Long) return (Long) id;
                    if (id instanceof Integer) return ((Integer) id).longValue();
                } catch (Exception ignored) {}
            }
        }
        return null;
    }

    @GetMapping
    public String showGalleryPage(Model model, HttpSession session) {
        Long artistId = getArtistIdFromSession(session);
        if (artistId == null) return "redirect:/login";

        Object artistObj = session.getAttribute("artist");
        if (artistObj == null) artistObj = session.getAttribute("loginArtist");
        if (artistObj == null) artistObj = session.getAttribute("loginUser");

        System.out.println(">>> [GET] DANG LAY TAC PHAM CHO ARTIST ID: " + artistId);

        model.addAttribute("myWorks", galleryRepository.findByArtistIdOrderByIdDesc(artistId));
        model.addAttribute("artist", artistObj);
        return "artist/gallery-manage";
    }

    @PostMapping("/upload")
    public String uploadWork(@RequestParam("title") String title,
                             @RequestParam("description") String description,
                             @RequestParam("imageFile") MultipartFile file,
                             HttpSession session) {
        Long artistId = getArtistIdFromSession(session);
        System.out.println(">>> [POST] ARTIST ID DANG DANG NHAP: " + artistId);

        if (artistId == null) return "redirect:/login";

        if (!file.isEmpty()) {
            try {
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) dir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                file.transferTo(new File(dir, fileName));

                Gallery gallery = new Gallery();
                gallery.setTitle(title);
                gallery.setDescription(description);
                gallery.setImageUrl(fileName);

                // Lấy MakeupArtist từ DB theo artistId
                MakeupArtist makeupArtist = makeupArtistRepository.findById(artistId).orElse(null);

                System.out.println("makeupArtist = " + makeupArtist);
                if (makeupArtist != null) {
                    gallery.setArtist(makeupArtist);
                    System.out.println("SET ARTIST = " + makeupArtist.getId());
                    galleryRepository.save(gallery);
                    System.out.println(">>> LUC NAY DA LUU THANH CONG VOI ARTIST ID: " + makeupArtist.getId());
                } else {
                    System.out.println(">>> LOI: KHONG TIM THAY MAKEUP ARTIST VOI ID = " + artistId + " TRONG BANG MAKEUP_ARTISTS!");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> LOI: FILE ANH BI RONG (EMPTY)!");
        }

        return "redirect:/artist/gallery-manage";
    }

    @GetMapping("/delete/{id}")
    public String deleteWork(@PathVariable("id") Long id, HttpSession session) {
        if (getArtistIdFromSession(session) == null) return "redirect:/login";

        galleryRepository.findById(id).ifPresent(gallery -> {
            File file = new File(UPLOAD_DIR, gallery.getImageUrl());
            if (file.exists()) file.delete();
            galleryRepository.delete(gallery);
        });

        return "redirect:/artist/gallery-manage";
    }
}