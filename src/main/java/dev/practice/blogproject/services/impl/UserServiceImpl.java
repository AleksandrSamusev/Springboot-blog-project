package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.mappers.UserMapper;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserShortDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserShortDto).collect(Collectors.toList());
    }

    @Override
    public UserShortDto getUserById(Long userId) {
        return new UserShortDto();
    }
}
