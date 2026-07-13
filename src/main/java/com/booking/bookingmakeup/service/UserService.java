package com.booking.bookingmakeup.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    // Kiểm tra email đã tồn tại chưa
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Lưu user
    public User save(User user) {
        return userRepository.save(user);
    }
    public User login(String email, String password) {

        return userRepository
                .findByEmailAndPassword(email, password)
                .orElse(null);
    }
}