package com.booking.bookingmakeup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.bookingmakeup.entity.MakeupService;

public interface ServiceRepository extends JpaRepository<MakeupService, Long> {

}