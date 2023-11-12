package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserNewDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.dtos.user.UserUpdateDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.InvalidParameterException;
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
    public Object getUserById(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found with given id " + userId));
        if (userId.equals(currentUserId)) {
            return UserMapper.toUserFullDto(user);
        }
        return UserMapper.toUserShortDto(user);
    }

    @Override
    public UserFullDto createUser(UserNewDto dto) {
        if (userRepository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail()) != null) {
            if (dto.getUsername().equals(userRepository
                    .findByUsernameOrEmail(dto.getUsername(), dto.getEmail()).getUsername())) {
                throw new InvalidParameterException("User with given Username already exists");
            } else {
                throw new InvalidParameterException("User with given email already exists");
            }
        }
        User user = UserMapper.toUser(dto);
        User savedUser = userRepository.save(user);
        log.info("User with id = " + savedUser.getUserId() + " created");
        return UserMapper.toUserFullDto(savedUser);
    }

    @Override
    public UserFullDto updateUser(Long userId, Long currentUserId, UserUpdateDto dto) {
        User userFromBd = userRepository.findById(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found with given ID " + userId));
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            userFromBd.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            userFromBd.setLastName(dto.getLastName());
        }
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            userFromBd.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            userFromBd.setEmail(dto.getEmail());
        }
        if (dto.getBirthDate() != null) {
            userFromBd.setBirthDate(dto.getBirthDate());
        }
        if (dto.getAbout() != null && !dto.getAbout().isBlank()) {
            userFromBd.setAbout(dto.getAbout());
        }

        User savedUser = userRepository.save(userFromBd);
        log.info("User with ID = " + savedUser.getUserId() + " updated");
        return UserMapper.toUserFullDto(savedUser);
    }

    @Override
    public void deleteUser(Long userId, Long currentUserId) {
        isUserPresent(userId);
        isUserPresent(currentUserId);
        isAuthorized(userId, currentUserId);
        userRepository.deleteById(userId);
    }

    private void isUserPresent(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User with given ID = " + userId + " not found");
        }
    }

    private void isAuthorized(Long userId, Long currentUserId) {
        if (userRepository.findById(currentUserId).isPresent()) {
            String role = userRepository.findById(currentUserId).get().getRole().name();
            if (!role.equals("ADMIN") && !userId.equals(currentUserId)) {
                throw new ActionForbiddenException("Action forbidden for current user");
            }
        }
    }
}
