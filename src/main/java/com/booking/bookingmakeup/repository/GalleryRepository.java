package com.booking.bookingmakeup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.bookingmakeup.entity.Gallery;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    
    List<Gallery> findByArtistIdOrderByIdDesc(Long artistId);
    List<Gallery> findAllByOrderByIdDesc();

    // Tìm kiếm tác phẩm theo tiêu đề
    List<Gallery> findByTitleContainingIgnoreCaseOrderByIdDesc(String keyword);
}

