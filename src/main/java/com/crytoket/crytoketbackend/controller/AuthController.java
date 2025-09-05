package com.crytoket.crytoketbackend.controller;

import com.crytoket.crytoketbackend.config.JwtUtil;
import com.crytoket.crytoketbackend.model.AuthRequest;
import com.crytoket.crytoketbackend.model.AuthResponse;
import com.crytoket.crytoketbackend.model.User;
import com.crytoket.crytoketbackend.model.VerificationRequest;
import com.crytoket.crytoketbackend.model.PasswordResetRequest;
import com.crytoket.crytoketbackend.repository.UserRepository;
import com.crytoket.crytoketbackend.service.EmailService;
import com.crytoket.crytoketbackend.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, MyUserDetailsService userDetailsService, UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthRequest authRequest) {
        Optional<User> existingUser = userRepository.findByEmail(authRequest.getEmail());
        if (existingUser.isPresent() && existingUser.get().isVerified()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        String otp = new Random().ints(100000, 999999)
                .findFirst()
                .getAsInt() + "";

        User user = existingUser.orElse(new User());
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setRole(authRequest.getRole());
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        user.setVerified(false);
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);

        return ResponseEntity.ok("OTP sent to your email. Please verify to complete registration.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerificationRequest verificationRequest) {
        User user = userRepository.findByEmail(verificationRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isVerified()) {
            return ResponseEntity.badRequest().body("User is already verified.");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP has expired.");
        }

        if (user.getOtp().equals(verificationRequest.getOtp())) {
            user.setVerified(true);
            user.setOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return ResponseEntity.ok("User verified successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP.");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (!user.isVerified()) {
            return ResponseEntity.badRequest().body("Please verify your account first.");
        }

        String otp = new Random().ints(100000, 999999)
                .findFirst()
                .getAsInt() + "";

        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        emailService.sendPasswordResetOtpEmail(user.getEmail(), otp);

        return ResponseEntity.ok("Password reset OTP sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest resetRequest) {
        User user = userRepository.findByEmail(resetRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp() == null || user.getOtpExpiry() == null) {
            return ResponseEntity.badRequest().body("Password reset not requested.");
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("OTP has expired.");
        }

        if (user.getOtp().equals(resetRequest.getOtp())) {
            user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));
            user.setOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return ResponseEntity.ok("Password has been reset successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Incorrect email or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}