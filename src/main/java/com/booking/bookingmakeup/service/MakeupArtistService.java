package com.booking.bookingmakeup.service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.booking.bookingmakeup.entity.MakeupArtist;
import com.booking.bookingmakeup.repository.MakeupArtistRepository;

@Service
public class MakeupArtistService {

    private final MakeupArtistRepository repository;

    public MakeupArtistService(MakeupArtistRepository repository) {
        this.repository = repository;
    }

    public List<MakeupArtist> getAll() {
        return repository.findAll();
    }

    public MakeupArtist getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public MakeupArtist save(MakeupArtist artist) {
        return repository.save(artist);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public boolean existsByEmail(String email) {
        return repository.findByEmail(email).isPresent();
    }

    public MakeupArtist login(String email, String password) {
        
        return repository
                .findByEmailAndPassword(email, password)
                .orElse(null);
    }

    public List<MakeupArtist> getActiveArtists() {
        return repository.findByActiveTrue();
    }
    public List<MakeupArtist> getAvailableArtists(
            LocalDate date,
            LocalTime time) {

        return repository.findAvailableArtists(date, time);
    }
}