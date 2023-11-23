package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.JWTAuthResponse;
import dev.practice.mainApp.dtos.user.LoginDto;
import dev.practice.mainApp.dtos.user.UserNewDto;

public interface AuthService {
    String register(UserNewDto userNewDto);
    JWTAuthResponse login(LoginDto loginDto);
}
