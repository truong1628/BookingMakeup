package com.booking.bookingmakeup.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.booking.bookingmakeup.entity.MakeupService;
import com.booking.bookingmakeup.repository.ServiceRepository;

@Service
public class ServiceService {

    private final ServiceRepository repository;

    public ServiceService(ServiceRepository repository) {
        this.repository = repository;
    }

    // Lấy tất cả dịch vụ (bao gồm cả dịch vụ đã ẩn - thường dùng cho Admin)
    public List<MakeupService> getAllServices() {
        return repository.findAll();
    }

    // Lấy danh sách dịch vụ đang hoạt động (dùng hiển thị cho Khách hàng đặt lịch)
    public List<MakeupService> getActiveServices() {
        return repository.findByActiveTrue();
    }

    public MakeupService getServiceById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public MakeupService save(MakeupService service) {
        return repository.save(service);
    }

    // Chuyển từ deleteById (Xóa cứng) sang Soft Delete (Đổi active = false)
    public void delete(Long id) {
        MakeupService service = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + id));
        
        service.setActive(false);
        repository.save(service);
    }

    // Đếm tổng số dịch vụ đang hoạt động
    public long countServices() {
        return repository.countByActiveTrue();
    }
}