package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserNewDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.dtos.user.UserUpdateDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.mappers.UserMapper;
import dev.practice.mainApp.models.Role;
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

    public List<UserFullDto> getAllUsers(Long currentUserId) {
        User currentUser = validations.checkUserExist(currentUserId);
        validations.checkUserIsAdmin(currentUser);
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
    public UserFullDto getUserById(Long userId, Long currentUserId) {
        User user = validations.checkUserExist(userId);
        User currentUser = validations.checkUserExist(currentUserId);
        String role = currentUser.getRole().name();
        if (userId.equals(currentUserId) || role.equals("ADMIN")) {
            log.info("Returned user with id = " + userId + " by Admin request");
            return UserMapper.toUserFullDto(user);
        } else {
            log.info("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }

    @Override
    public UserFullDto createUser(UserNewDto dto) {
        String username = dto.getUsername().replaceAll("\\s+", "");
        String email = dto.getEmail().toLowerCase();
        dto.setUsername(username);
        dto.setEmail(email);
        if (userRepository.findByUsernameOrEmail(dto.getUsername(), dto.getEmail()) != null) {
            if (dto.getUsername().trim().equalsIgnoreCase(userRepository
                    .findByUsernameOrEmail(dto.getUsername(),
                            dto.getEmail()).getUsername()
                    .trim())) {
                log.info("InvalidParameterException. User with given Username = " +
                        dto.getUsername() + "already exists");
                throw new InvalidParameterException("User with given Username already exists");
            } else {
                log.info("InvalidParameterException. User with given email = " +
                        dto.getEmail() + "already exists");
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
        User userFromBd = validations.checkUserExist(userId);
        User currentUser = validations.checkUserExist(currentUserId);
        if (!userFromBd.getUserId().equals(currentUser.getUserId())) {
            log.info("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
        }
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            userFromBd.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            userFromBd.setLastName(dto.getLastName());
        }
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            String uName = dto.getUsername().replaceAll("\\s+", "");
            if (uName.equalsIgnoreCase(userFromBd.getUsername())) { // check logic
                throw new ActionForbiddenException("User with given Username already exists");
            }
            userFromBd.setUsername(uName);
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (userRepository.findByEmail(dto.getEmail()
                    .replaceAll("\\s+", "").toLowerCase()).isPresent()) {
                throw new ActionForbiddenException("User with given email already exists");
            }
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
        validations.checkUserIsAdmin(currentUser);
        user.setIsBanned(Boolean.TRUE);
        User savedUser = userRepository.save(user);
        log.info("User with ID = " + userId + " was banned by admin with ID = " + currentUserId);
        return UserMapper.toUserFullDto(savedUser);
    }

    @Override
    public UserFullDto unbanUser(Long userId, Long currentUserId) {
        User user = validations.checkUserExist(userId);
        User currentUser = validations.checkUserExist(currentUserId);
        validations.checkUserIsAdmin(currentUser);
        user.setIsBanned(Boolean.FALSE);
        User savedUser = userRepository.save(user);
        log.info("User with ID = " + userId + " was unbanned by admin with ID = " + currentUserId);
        return UserMapper.toUserFullDto(savedUser);
    }

    @Override
    public UserFullDto changeRole(Long userId, String role) {
        User user = validations.checkUserExist(userId);
        if(role.equals("ADMIN")) {
            user.setRole(Role.ADMIN);
        } else {
            user.setRole(Role.USER);
        }
        User savedUser = userRepository.save(user);
        log.info("The role of the user with ID = {} has changed to ADMIN", userId);
        return UserMapper.toUserFullDto(savedUser);
    }
}