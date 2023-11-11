package dev.practice.blogproject.services;

import dev.practice.blogproject.dtos.user.UserShortDto;

import java.util.List;

public interface UserService {
    List<UserShortDto> getAllUsers();

    UserShortDto getUserById(Long id);
}
