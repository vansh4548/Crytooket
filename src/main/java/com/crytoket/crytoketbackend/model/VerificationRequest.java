package com.crytoket.crytoketbackend.model;

import lombok.Data;

@Data
public class VerificationRequest {
    private String email;
    private String otp;
}