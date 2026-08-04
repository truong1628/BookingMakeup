package com.booking.bookingmakeup.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.booking.bookingmakeup.entity.Booking;
import com.booking.bookingmakeup.entity.MakeupArtist;
import com.booking.bookingmakeup.entity.User;

public interface BookingRepository extends JpaRepository<Booking, Long>{

    List<Booking> findByUser(User user);
    List<Booking> findByStatus(String status);

    List<Booking> findByArtistIdOrderByBookingDateAscBookingTimeAsc(Long artistId);    
    boolean existsByArtistAndBookingDateAndBookingTime(
        MakeupArtist artist,
        LocalDate bookingDate,
        LocalTime bookingTime);
    @Query("""
        select b.bookingTime
        from Booking b
        where b.artist = :artist
        and b.bookingDate = :date
        and b.status <> 'Cancelled'
        """)
    List<LocalTime> findBookedTimesByArtistAndDate(
                @Param("artist") MakeupArtist artist,
                @Param("date") LocalDate date);
    long countByStatus(String status);   

    boolean existsByArtistAndBookingDateAndBookingTimeAndIdNot(
            MakeupArtist artist,
            LocalDate bookingDate,
            LocalTime bookingTime,
            Long id);

    List<Booking> findByArtistId(Long artistId);
    List<Booking> findByArtistIdOrderByIdDesc(Long artistId);

    @Query("""
        SELECT b.artist.fullName, SUM(b.service.price)
        FROM Booking b
        WHERE b.status = 'COMPLETED'
        GROUP BY b.artist.fullName
        ORDER BY 2 DESC
        """)
    List<Object[]> getArtistRevenue();

    // 1. Doanh thu theo Ngày
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status = 'COMPLETED' AND CAST(b.bookingDate AS date) = :date")
    Double getRevenueByDate(@Param("date") LocalDate date);

    // 2. Doanh thu theo Tháng và Năm
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status = 'COMPLETED' AND MONTH(b.bookingDate) = :month AND YEAR(b.bookingDate) = :year")
    Double getRevenueByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // 3. Doanh thu theo Năm
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status = 'COMPLETED' AND YEAR(b.bookingDate) = :year")
    Double getRevenueByYear(@Param("year") int year);

    // 4. Tổng Doanh thu Tích lũy
    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status = 'COMPLETED'")
    Double getTotalRevenue();

    // 1. Đếm tổng booking của Artist
        @Query("SELECT COUNT(b) FROM Booking b WHERE b.artist.id = :artistId")
        Long countTotalByArtist(@Param("artistId") Long artistId);

        // 2. Đếm số đơn đã hoàn thành của Artist
        @Query("SELECT COUNT(b) FROM Booking b WHERE b.artist.id = :artistId AND b.status = 'COMPLETED'")
        Long countCompletedByArtist(@Param("artistId") Long artistId);

        // 3. Đếm số đơn đang thực hiện (đã xác nhận)
        @Query("SELECT COUNT(b) FROM Booking b WHERE b.artist.id = :artistId AND b.status = 'CONFIRMED'")
        Long countConfirmedByArtist(@Param("artistId") Long artistId);

        // 4. Doanh thu tháng này của Artist
        @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.artist.id = :artistId AND b.status = 'COMPLETED' AND MONTH(b.bookingDate) = MONTH(CURRENT_DATE) AND YEAR(b.bookingDate) = YEAR(CURRENT_DATE)")
        Double getMonthRevenueByArtist(@Param("artistId") Long artistId);
    
}