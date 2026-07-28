package com.booking.bookingmakeup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "galleries") 
public class Gallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String imageUrl;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private MakeupArtist artist; // Đã đổi thành MakeupArtist

    public Gallery() {}

    public Gallery(String title, String imageUrl, String description, MakeupArtist artist) { // Đã đổi tham số
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.artist = artist;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Đã cập nhật Getter và Setter cho MakeupArtist
    public MakeupArtist getArtist() { return artist; }
    public void setArtist(MakeupArtist artist) { this.artist = artist; }
}