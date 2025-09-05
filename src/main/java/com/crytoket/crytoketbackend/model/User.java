package com.crytoket.crytoketbackend.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String role;
    private String otp;
    private LocalDateTime otpExpiry;
    private boolean verified = false;

    // Profile fields
    private String name;
    private String phone;
    private String aadhaar;
    private String pan;
    private String education;
    private String designation;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String profileImage; // Stores the Base64 encoded image string

    private String instagramUrl;
    private String facebookUrl;
    private String linkedinUrl;
    private String twitterUrl;
}