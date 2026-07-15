package com.booking.bookingmakeup.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.booking.bookingmakeup.entity.MakeupArtist;
import com.booking.bookingmakeup.repository.MakeupArtistRepository;

@Service
public class MakeupArtistService {

    private final MakeupArtistRepository repository;

    public MakeupArtistService(
            MakeupArtistRepository repository) {

        this.repository = repository;
    }

    public List<MakeupArtist> getAll(){

        return repository.findAll();

    }

    public MakeupArtist getById(Long id){

        return repository.findById(id).orElse(null);

    }

}