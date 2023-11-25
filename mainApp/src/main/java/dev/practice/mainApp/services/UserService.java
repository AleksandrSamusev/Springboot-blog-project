package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.dtos.user.UserUpdateDto;
import dev.practice.mainApp.models.Role;
import org.springframework.http.HttpStatusCode;


import java.util.List;

public interface UserService {
    List<UserShortDto> getAllUsers();

    List<UserFullDto> getAllUsers(String username);

    UserShortDto getUserById(Long id);

    UserFullDto getUserById(Long userId, String username);

    void deleteUser(Long userId, Long currentUserId);

    UserFullDto banUser(Long userId, Long currentUserId);

    UserFullDto unbanUser(Long userId, Long currentUserId);

    UserFullDto updateUser(Long userId, UserUpdateDto dto, String username);
}
