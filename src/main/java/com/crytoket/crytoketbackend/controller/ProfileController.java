package com.crytoket.crytoketbackend.controller;

import com.crytoket.crytoketbackend.model.User;
import com.crytoket.crytoketbackend.model.UserProfileDto;
import com.crytoket.crytoketbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile") // Correct endpoint for profile operations
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDto dto = new UserProfileDto();
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setPhone(user.getPhone());
        dto.setAadhaar(user.getAadhaar());
        dto.setPan(user.getPan());
        dto.setEducation(user.getEducation());
        dto.setDesignation(user.getDesignation());
        dto.setProfileImage(user.getProfileImage());
        dto.setInstagramUrl(user.getInstagramUrl());
        dto.setFacebookUrl(user.getFacebookUrl());
        dto.setLinkedinUrl(user.getLinkedinUrl());
        dto.setTwitterUrl(user.getTwitterUrl());

        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<?> updateUserProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserProfileDto profileUpdate) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(profileUpdate.getName());
        user.setPhone(profileUpdate.getPhone());
        user.setAadhaar(profileUpdate.getAadhaar());
        user.setPan(profileUpdate.getPan());
        user.setEducation(profileUpdate.getEducation());
        user.setDesignation(profileUpdate.getDesignation());
        user.setProfileImage(profileUpdate.getProfileImage());
        user.setInstagramUrl(profileUpdate.getInstagramUrl());
        user.setFacebookUrl(profileUpdate.getFacebookUrl());
        user.setLinkedinUrl(profileUpdate.getLinkedinUrl());
        user.setTwitterUrl(profileUpdate.getTwitterUrl());

        userRepository.save(user);

        return ResponseEntity.ok("Profile updated successfully");
    }
}