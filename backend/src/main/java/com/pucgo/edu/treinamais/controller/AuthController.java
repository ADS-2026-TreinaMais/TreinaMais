package com.pucgo.edu.treinamais.controller;

import com.pucgo.edu.treinamais.dto.request.LoginRequest;
import com.pucgo.edu.treinamais.dto.request.RegisterRequest;
import com.pucgo.edu.treinamais.dto.response.AuthResponse;
import com.pucgo.edu.treinamais.dto.response.MessageResponse;
import com.pucgo.edu.treinamais.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping({"/register", "/cadastro"})
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        MessageResponse response = authService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.autenticar(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/refresh", "/renovar"})
    public ResponseEntity<AuthResponse> refresh(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) com.pucgo.edu.treinamais.dto.request.RefreshTokenRequest request) {
        
        String tokenToRefresh = null;
        if (request != null && request.getToken() != null && !request.getToken().isBlank()) {
            tokenToRefresh = request.getToken();
        } else if (authHeader != null && !authHeader.isBlank()) {
            tokenToRefresh = authHeader;
        }

        AuthResponse response = authService.renovar(tokenToRefresh);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) com.pucgo.edu.treinamais.dto.request.RefreshTokenRequest request) {

        String tokenToRevoke = null;
        if (request != null && request.getToken() != null && !request.getToken().isBlank()) {
            tokenToRevoke = request.getToken();
        } else if (authHeader != null && !authHeader.isBlank()) {
            tokenToRevoke = authHeader;
        }

        MessageResponse response = authService.logout(tokenToRevoke);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(Map.of(
                "email", userDetails.getUsername(),
                "authorities", userDetails.getAuthorities().toString()
        ));
    }
}
