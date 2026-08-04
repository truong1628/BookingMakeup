package com.booking.bookingmakeup.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 20)
    private String locationType; // "STUDIO" hoặc "HOME"

    @NotNull(message = "Vui lòng chọn ngày")
    @FutureOrPresent(message = "Ngày phải từ hôm nay trở đi")
    private LocalDate bookingDate;

    @NotNull(message = "Vui lòng chọn giờ")
    private LocalTime bookingTime;
    
    private String status; // "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private MakeupService service;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private MakeupArtist artist;

    private String cancelReason;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String address;

    @Column(length = 15)
    private String phone;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String note;

    @Column
    private Double distanceKm = 0.0;

    @Column
    private Double travelFee = 0.0;

    @Column
    private Double totalPrice = 0.0;

    @Column
    private Double depositAmount = 0.0;

    @Column(length = 30)
    private String paymentType; // "PAY_LATER", "DEPOSIT", "FULL"

    @Column(length = 30)
    private String paymentStatus; // "UNPAID", "PENDING_APPROVAL", "PAID"

    // 🟢 THÊM: Trường tính toán số tiền còn lại cần thu (Không lưu vào Database)
    @Transient
    private Double remainingAmount;

    public Booking() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MakeupService getService() {
        return service;
    }

    public void setService(MakeupService service) {
        this.service = service;
    }

    public MakeupArtist getArtist() {
        return artist;
    }

    public void setArtist(MakeupArtist artist) {
        this.artist = artist;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getTravelFee() {
        return travelFee;
    }

    public void setTravelFee(Double travelFee) {
        this.travelFee = travelFee;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(Double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    // 🟢 THÊM: Getter và Setter cho remainingAmount
    public Double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
}