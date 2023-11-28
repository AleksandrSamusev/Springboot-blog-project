package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.dtos.user.UserUpdateDto;
import dev.practice.mainApp.models.Role;
import org.springframework.http.HttpStatusCode;


import java.util.List;

public interface UserService {
    List<UserShortDto> getAllUsers();

    List<UserFullDto> getAllUsers(String login);

    UserShortDto getUserById(Long id);

    UserFullDto getUserById(Long userId, String login);

    void deleteUser(Long userId, String login);

    UserFullDto banUser(Long userId, String login);

    UserFullDto unbanUser(Long userId, String login);

    UserFullDto updateUser(Long userId, UserUpdateDto dto, String login);

    UserFullDto changeRole(Long userId, String role);
}
