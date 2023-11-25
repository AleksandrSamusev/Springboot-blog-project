package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserShortDto;

import java.util.List;

public interface UserService {
    List<UserShortDto> getAllUsers();

    List<UserFullDto> getAllUsers(String username);

    UserShortDto getUserById(Long id);

    UserFullDto getUserById(Long userId, String username);

    void deleteUser(Long userId, Long currentUserId);

    UserFullDto banUser(Long userId, Long currentUserId);

    UserFullDto unbanUser(Long userId, Long currentUserId);
}
