package dev.practice.blogproject.services;

import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserNewDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.dtos.user.UserUpdateDto;

import java.util.List;

public interface UserService {
    List<UserShortDto> getAllUsers();

    List<UserFullDto> getAllUsers(Long currentUserId);

    UserShortDto getUserById(Long id);

    UserFullDto getUserById(Long userId, Long id);

    UserFullDto createUser(UserNewDto dto);

    UserFullDto updateUser(Long userId, Long currentUserId, UserUpdateDto dto);

    void deleteUser(Long userId, Long currentUserId);
}
