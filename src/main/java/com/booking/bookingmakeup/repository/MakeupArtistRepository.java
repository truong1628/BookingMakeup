package com.booking.bookingmakeup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.bookingmakeup.entity.MakeupArtist;

public interface MakeupArtistRepository
        extends JpaRepository<MakeupArtist,Long>{

}