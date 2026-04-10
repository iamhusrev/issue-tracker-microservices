package com.iamhusrev.controller;

import com.iamhusrev.dto.*;
import com.iamhusrev.entity.ResponseWrapper;
import com.iamhusrev.exception.UserServiceException;
import com.iamhusrev.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper> register(@Valid @RequestBody RegisterRequestDTO request)
            throws UserServiceException {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseWrapper("User registered successfully", response, HttpStatus.CREATED));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper> login(@Valid @RequestBody LoginRequestDTO request)
            throws UserServiceException {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(new ResponseWrapper("Login successful", response, HttpStatus.OK));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseWrapper> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request)
            throws UserServiceException {
        AuthResponseDTO response = authService.refreshToken(request);
        return ResponseEntity.ok(new ResponseWrapper("Token refreshed successfully", response, HttpStatus.OK));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseWrapper> logout(@Valid @RequestBody RefreshTokenRequestDTO request) {
        authService.logout(request);
        return ResponseEntity.ok(new ResponseWrapper("Logged out successfully", HttpStatus.OK));
    }
}
