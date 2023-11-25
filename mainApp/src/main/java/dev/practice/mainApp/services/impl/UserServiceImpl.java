package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.dtos.user.UserUpdateDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.mappers.UserMapper;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.UserService;
import dev.practice.mainApp.utils.Validations;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Validations validations;

    @Override
    public List<UserShortDto> getAllUsers() {
        log.info("Returned the List of all UserShortDto users by User request");
        return userRepository.findAll().stream().map(UserMapper::toUserShortDto).collect(Collectors.toList());
    }

    public List<UserFullDto> getAllUsers(String username) {
        User user = userRepository.findByUsername(username);
        log.info("Returned the List of all UserFullDto users by Admin request");
        return userRepository.findAll().stream().map(UserMapper::toUserFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserShortDto getUserById(Long userId) {
        User user = validations.checkUserExist(userId);
        log.info("Returned user with id = " + userId + " by User request");
        return UserMapper.toUserShortDto(user);
    }

    @Override
    public UserFullDto getUserById(Long userId, String username) {
        User user = validations.checkUserExist(userId);
        if (userId.equals(userRepository.findByUsername(username).getUserId()) || validations.isAdmin(username)) {
            log.info("Returned user with id = " + userId + " by Admin request");
            return UserMapper.toUserFullDto(user);
        } else {
            log.info("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }


    @Override
    public void deleteUser(Long userId, Long currentUserId) {
        validations.checkUserExist(userId);
        User currentUser = validations.checkUserExist(currentUserId);
        validations.isUserAuthorized(userId, currentUser);
        log.info("User with ID = " + userId + " deleted");
        userRepository.deleteById(userId);
    }

    @Override
    public UserFullDto banUser(Long userId, Long currentUserId) {
        User user = validations.checkUserExist(userId);
        User currentUser = validations.checkUserExist(currentUserId);
        user.setIsBanned(Boolean.TRUE);
        User savedUser = userRepository.save(user);
        log.info("User with ID = " + userId + " was banned by admin with ID = " + currentUserId);
        return UserMapper.toUserFullDto(savedUser);
    }

    @Override
    public UserFullDto unbanUser(Long userId, Long currentUserId) {
        User user = validations.checkUserExist(userId);
        User currentUser = validations.checkUserExist(currentUserId);
        user.setIsBanned(Boolean.FALSE);
        User savedUser = userRepository.save(user);
        log.info("User with ID = " + userId + " was unbanned by admin with ID = " + currentUserId);
        return UserMapper.toUserFullDto(savedUser);
    }

    @Override
    public UserFullDto updateUser(Long userId, UserUpdateDto dto, String username) {

        User requester = validations.checkUserExistsByUsernameOrEmail(username);
        User user = validations.checkUserExist(userId);

        if (userId.equals(requester.getUserId()) || validations.isAdmin(username)) {
            if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
                user.setFirstName(dto.getFirstName());
            }
            if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
                user.setLastName(dto.getLastName());
            }
            if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
                if (validations.usernameAlreadyExists(dto.getUsername())) {
                    throw new InvalidParameterException(
                            "User with given username " + dto.getUsername() + " already exists");
                } else {
                    user.setUsername(dto.getUsername().trim().replaceAll("\\s+",""));
                }
            }
            if(dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(dto.getPassword());
            }
            if(dto.getBirthDate() != null) {
                user.setBirthDate(dto.getBirthDate());
            }
            if (dto.getAbout() != null && !dto.getAbout().isBlank()) {
                user.setAbout(dto.getAbout());
            }
        } else {
            throw new ActionForbiddenException("Action forbidden for current user");
        }
        User savedUser = userRepository.save(user);
        log.info("User with ID = {} was successfully updated", savedUser.getUserId());
        return UserMapper.toUserFullDto(savedUser);
    }
}