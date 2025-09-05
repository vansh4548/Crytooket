package com.crytoket.crytoketbackend.model;

import lombok.Data;

@Data
public class UserProfileDto {
    private String email;
    private String name;
    private String phone;
    private String aadhaar;
    private String pan;
    private String education;
    private String designation;
    private String profileImage; // Will carry the Base64 string
    private String instagramUrl;
    private String facebookUrl;
    private String linkedinUrl;
    private String twitterUrl;
}