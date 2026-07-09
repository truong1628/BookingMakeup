package com.booking.bookingmakeup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.booking.bookingmakeup.repository.UserRepository;

@SpringBootApplication
public class BookingmakeupApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingmakeupApplication.class, args);
    }

    @Bean
    CommandLineRunner test(UserRepository repository) {
        return args -> {
            System.out.println("===========");
            System.out.println("User count = " + repository.count());
            System.out.println("===========");
        };
    }
}