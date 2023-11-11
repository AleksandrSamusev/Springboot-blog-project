package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserNewDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.mappers.UserMapper;
import dev.practice.blogproject.models.User;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserShortDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserShortDto).collect(Collectors.toList());
    }

    @Override
    public UserShortDto getUserById(Long userId) {
        Optional<User> dto = userRepository.findById(userId);
        if (dto.isEmpty()) {
            throw new ResourceNotFoundException("User with given id " + userId + " not found");
        }
        return UserMapper.toUserShortDto(dto.get());
    }

    @Override
    public UserFullDto createUser(UserNewDto dto) {
        User user = UserMapper.toUser(dto);
        User savedUser = userRepository.save(user);
        log.info("User with id = " + savedUser.getUserId() + " created");
        return UserMapper.toUserFullDto(savedUser);
    }
}
