package com.booking.bookingmakeup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "services")
public class MakeupService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên dịch vụ không được để trống")
    @Column(name = "service_name")
    private String serviceName;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "1.0", message = "Giá phải lớn hơn 0")
    private Double price;

    @NotNull(message = "Thời gian không được để trống")
    @Min(value = 1, message = "Thời gian phải lớn hơn 0")
    private Integer duration;

    @NotBlank(message = "Mô tả không được để trống")
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @NotBlank(message = "Ảnh không được để trống")
    private String image;


    public MakeupService() {
    }

    public Long getId() { return id; }
    public void setId(Long id) {this.id = id;}
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}