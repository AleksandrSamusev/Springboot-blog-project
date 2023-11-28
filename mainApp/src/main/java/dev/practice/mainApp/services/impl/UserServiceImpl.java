package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.dtos.user.UserUpdateDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.mappers.UserMapper;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.Role;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.ArticlePrivateService;
import dev.practice.mainApp.services.UserService;
import dev.practice.mainApp.utils.Validations;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ArticlePrivateService articleService;
    private final Validations validations;
    private final BCryptPasswordEncoder encoder;

    @Override
    public List<UserShortDto> getAllUsers() {
        log.info("Returned the List of all UserShortDto users by User request");
        return userRepository.findAll().stream().map(UserMapper::toUserShortDto).collect(Collectors.toList());
    }

    public List<UserFullDto> getAllUsers(String login) {
        validations.checkUserExistsByUsernameOrEmail(login);
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
    public UserFullDto getUserById(Long userId, String login) {
        User user = validations.checkUserExist(userId);
        User currentUser = validations.checkUserExistsByUsernameOrEmail(login);
        if (user.equals(currentUser) || validations.isAdmin(login)) {
            log.info("Returned user with id = " + userId + " by Admin request");
            return UserMapper.toUserFullDto(user);
        } else {
            log.info("ActionForbiddenException. Action forbidden for current user");
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }

    @Override
    public void deleteUser(Long userId, String login) {
        User requester = validations.checkUserExistsByUsernameOrEmail(login);
        User user = validations.checkUserExist(userId);
        if (userId.equals(requester.getUserId()) || validations.isAdmin(login)) {
            user.getArticles().forEach(article ->
                    articleService.deleteArticle(login, article.getArticleId()));
            user.setRoles(new HashSet<>());
            User savedUser = userRepository.save(user);
            userRepository.deleteById(savedUser.getUserId());
            log.info("User with ID = " + userId + " was successfully deleted.");
        } else {
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }

    @Override
    public UserFullDto banUser(Long userId, String login) {
        User user = validations.checkUserExist(userId);
        User currentUser = validations.checkUserExistsByUsernameOrEmail(login);
        if (validations.isAdmin(login)) {
            user.setIsBanned(Boolean.TRUE);
            User savedUser = userRepository.save(user);
            log.info("User with ID = " + userId + " was banned by admin with ID = " + currentUser.getUserId());
            return UserMapper.toUserFullDto(savedUser);
        } else {
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }

    @Override
    public UserFullDto unbanUser(Long userId, String login) {
        User user = validations.checkUserExist(userId);
        User currentUser = validations.checkUserExistsByUsernameOrEmail(login);
        if (validations.isAdmin(login)) {
            user.setIsBanned(Boolean.FALSE);
            User savedUser = userRepository.save(user);
            log.info("User with ID = " + userId + " was unbanned by admin with ID = " + currentUser.getUserId());
            return UserMapper.toUserFullDto(savedUser);
        } else {
            throw new ActionForbiddenException("Action forbidden for current user");
        }
    }

    @Override
    public UserFullDto updateUser(Long userId, UserUpdateDto dto, String login) {

        User requester = validations.checkUserExistsByUsernameOrEmail(login);
        User user = validations.checkUserExist(userId);

        if (userId.equals(requester.getUserId()) || validations.isAdmin(login)) {
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
                    user.setUsername(dto.getUsername().trim().replaceAll("\\s+", ""));
                }
            }
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                user.setPassword(encoder.encode(dto.getPassword()));
            }
            if (dto.getBirthDate() != null) {
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

    @Override
    public UserFullDto changeRole(Long userId, String roleName) {
        User user = validations.checkUserExist(userId);
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isPresent()) {
            user.getRoles().add(role.get());
        } else {
            throw new ResourceNotFoundException("Role with given name: '" + roleName + "' not found");
        }
        User savedUser = userRepository.save(user);
        log.info("Set new role: {} to user with ID: {}", roleName, userId);
        return UserMapper.toUserFullDto(savedUser);
    }
}