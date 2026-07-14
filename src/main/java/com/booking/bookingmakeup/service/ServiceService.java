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

    public List<MakeupService> getAllServices() {
        return repository.findAll();
    }
    public MakeupService getServiceById(Long id) {
        return repository.findById(id).orElse(null);
    }
    public MakeupService save(MakeupService service) {
        return repository.save(service);
    }
    public void delete(Long id) {
        repository.deleteById(id);
    }
}