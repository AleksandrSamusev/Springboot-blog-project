package dev.practice.mainApp.controllers;

import dev.practice.mainApp.dtos.JWTAuthResponse;
import dev.practice.mainApp.dtos.user.LoginDto;
import dev.practice.mainApp.dtos.user.UserNewDto;
import dev.practice.mainApp.services.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // Build register REST API
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserNewDto userNewDto) {
        String response = authService.register(userNewDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Build login REST API
    @PostMapping("/login")
    public ResponseEntity<JWTAuthResponse> login(@RequestBody LoginDto loginDto) {
        JWTAuthResponse jwtAuthResponse = authService.login(loginDto);
        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }
    //change
}
