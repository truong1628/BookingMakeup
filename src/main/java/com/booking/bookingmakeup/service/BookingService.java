package com.booking.bookingmakeup.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.MakeupArtist;
import com.booking.bookingmakeup.entity.User;
import com.booking.bookingmakeup.repository.BookingRepository;
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MakeupArtistService artistService;

    public BookingService(
            BookingRepository bookingRepository,
            MakeupArtistService artistService) {

        this.bookingRepository = bookingRepository;
        this.artistService = artistService;
    }

    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(User user){
        return bookingRepository.findByUser(user);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    // User hủy lịch
    public void cancelBooking(Long id, User user) {

        Booking booking = getBookingById(id);
        if (booking == null) {
            return;
        }

        if (!booking.getUser().getId().equals(user.getId())) {
            return;
        }
        if (!"PENDING".equals(booking.getStatus())) {
            return;
        }

        booking.setStatus("CANCELLED");

        bookingRepository.save(booking);
    }


 // Admin hủy
    public void adminCancelBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking == null) {
            return;
        }

       if ("CANCELLED".equals(booking.getStatus())) {
            return;
        }

        booking.setStatus("CANCELLED");

        bookingRepository.save(booking);
    }

    public boolean existsBooking(
            MakeupArtist artist,
            LocalDate date,
            LocalTime time){

        return bookingRepository
                .existsByArtistAndBookingDateAndBookingTime(
                        artist,
                        date,
                        time);
    }

    public List<LocalTime> getBookedTimes(
            MakeupArtist artist,
            LocalDate date) {

        return bookingRepository.findBookedTimesByArtistAndDate(
                artist,
                date);
    }
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }


    public void confirmBooking(Long id) {

        Booking booking = getBookingById(id);

        if (booking == null) {
            return;
        }

        // Chỉ xác nhận khi đang chờ
        if (!"PENDING".equals(booking.getStatus())) {
            return;
        }

        // Chưa có Artist thì không được xác nhận
        if (booking.getArtist() == null) {
            throw new RuntimeException("Chưa phân công Makeup Artist.");
        }

        booking.setStatus("CONFIRMED");

        bookingRepository.save(booking);
    }
   
    
    public long countAll() {
         return bookingRepository.count();
    }

    public long countPending() {
        return bookingRepository.countByStatus("PENDING");
    }

    public long countConfirmed() {
        return bookingRepository.countByStatus("CONFIRMED");
    }

    public long countCompleted() {
        return bookingRepository.countByStatus("COMPLETED");
    }

    public long countCancelled() {
        return bookingRepository.countByStatus("CANCELLED");
    }
    public List<Booking> getBookingsByArtist(Long artistId) {

        return bookingRepository
                .findByArtistIdOrderByBookingDateAscBookingTimeAsc(artistId);

    }

    public void startBooking(Long id) {

        Booking booking = getBookingById(id);

        booking.setStatus("IN_PROGRESS");

        bookingRepository.save(booking);
    }
    public void finishBooking(Long id) {

        Booking booking = getBookingById(id);

        booking.setStatus("COMPLETED");

        bookingRepository.save(booking);
    }

    public void assignArtist(Long bookingId, Long artistId) {

    Booking booking = getBookingById(bookingId);

    if (booking == null) {
        return;
    }

    MakeupArtist artist = artistService.getById(artistId);

    if (artist == null) {
        return;
    }

    // Kiểm tra artist có bận không
    if (bookingRepository.existsByArtistAndBookingDateAndBookingTimeAndIdNot(
            artist,
            booking.getBookingDate(),
            booking.getBookingTime(),
            booking.getId())) {

        throw new RuntimeException("Makeup Artist đã có lịch.");
    }

    booking.setArtist(artist);

    bookingRepository.save(booking);
}
// Thêm hàm này để đồng bộ với AdminBookingController
    public void completeBooking(Long id) {
        Booking booking = getBookingById(id);
        if (booking == null) {
            return;
        }
        
        // Cập nhật trạng thái thành chữ "COMPLETED" viết hoa chữ đầu giống logic check ở HTML của bạn
        booking.setStatus("COMPLETED");
        bookingRepository.save(booking);
    }
    // Trong BookingService.java
    public List<Booking> getBookingsByArtistId(Long artistId) {
        return bookingRepository.findByArtistIdOrderByIdDesc(artistId);
    }
    public void requestCancelBooking(Long bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));
        
        booking.setStatus("CANCEL_REQUESTED"); // Trạng thái Chờ Admin duyệt hủy
        booking.setCancelReason(reason);
        bookingRepository.save(booking);
    }

    // Trong BookingService.java

    public void approveCancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));
        
        booking.setStatus("CANCELLED"); // Chấp nhận cho hủy
        bookingRepository.save(booking);
    }

    public void rejectCancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));
        
        booking.setStatus("CONFIRMED"); // Từ chối hủy -> Quay về trạng thái Đã xác nhận
        bookingRepository.save(booking);
    }
    
}