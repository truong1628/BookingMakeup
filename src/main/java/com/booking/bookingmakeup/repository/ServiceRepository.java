package com.booking.bookingmakeup.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.booking.bookingmakeup.entity.MakeupService;

@Repository
public interface ServiceRepository extends JpaRepository<MakeupService, Long> {

    // Lấy danh sách tất cả các dịch vụ đang hoạt động (active = true)
    List<MakeupService> findByActiveTrue();

    // Đếm số lượng dịch vụ đang hoạt động
    long countByActiveTrue();
}