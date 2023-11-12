package dev.practice.blogproject;

import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserNewDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.models.*;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.impl.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepositoryMock;
    @InjectMocks
    UserServiceImpl userService;

    private final User user1 = new User(1L, "John", "Doe",
            "johnDoe", "johnDoe@test.test",
            LocalDate.of(2000, 12, 27), Role.USER, "Hi! I'm John", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final User user2 = new User(2L, "Marry", "Dawson",
            "marryDawson", "merryDawson@test.test",
            LocalDate.of(1995, 6, 14), Role.ADMIN, "Hi! I'm Marry", true,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final User user3 = new User(3L, "Harry", "Potter",
            "harryPotter", "harryPotter@test.test",
            LocalDate.of(1901, 5, 13), Role.USER, "Hi! I'm Harry", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final UserNewDto newUser3 = new UserNewDto("Harry", "Potter",
            "johnDoe", "harryPotter@test.test",
            LocalDate.of(1901, 5, 13), "Hi! I'm Harry");

    private final UserNewDto newUser4 = new UserNewDto("Harry", "Potter",
            "Kirk", "johnDoe@test.test",
            LocalDate.of(1901, 5, 13), "Hi! I'm Harry");


    @Test
    public void user_test_1_When_getAllUsers_Then_returnListOfAllUsers() {
        when(userRepositoryMock.findAll()).thenReturn(List.of(user1, user2));

        List<UserShortDto> result = userService.getAllUsers();

        assertEquals(result.get(0).getUserName(), user1.getUsername());
        assertEquals(result.get(1).getUserName(), user2.getUsername());
        verify(userRepositoryMock, times(1)).findAll();
    }

    @Test
    public void user_test_2_Given_ValidUserId_When_getUserById_Then_returnValidUser() {
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepositoryMock.findById(2L)).thenReturn(Optional.of(user2));

        UserShortDto dto1 = userService.getUserById(1L);
        UserShortDto dto2 = userService.getUserById(2L);

        assertEquals(dto1.getUserName(), user1.getUsername());
        assertEquals(dto2.getUserName(), user2.getUsername());

        verify(userRepositoryMock, times(2)).findById(anyLong());
    }

    @Test
    public void user_test_3_Given_InvalidUserId_When_getUserById_Then_ResourceNotFoundException() {

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(2L);
        });

        assertEquals("User with given id 2 not found", thrown.getMessage());
    }

    @Test
    public void user_test_4_Given_ValidUserIdAndCurrentUserId_When_getUserById_Then_returnValidUser() {
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepositoryMock.findById(2L)).thenReturn(Optional.of(user2));

        assertEquals(userService.getUserById(1L, 1L).getClass(), UserFullDto.class);
        assertEquals(userService.getUserById(1L, 2L).getClass(), UserFullDto.class);
    }

    @Test
    public void user_test_5_Given_UserWithRoleAsUser_When_getUserById_Then_ActionForbiddenException() {
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepositoryMock.findById(3L)).thenReturn(Optional.of(user3));

        ActionForbiddenException thrown = assertThrows(ActionForbiddenException.class, () ->
                userService.getUserById(1L, user3.getUserId()));
        assertEquals("Action forbidden for current user", thrown.getMessage());
    }

    @Test
    public void user_test_6_Given_ValidUser_When_createUser_Then_userCreated() {
        when(userRepositoryMock.findByUsernameOrEmail(anyString(), anyString())).thenReturn(null);
        when(userRepositoryMock.save(any())).thenReturn(user3);

        UserFullDto createdUser = userService.createUser(newUser3);

        assertEquals(createdUser.getClass(), UserFullDto.class);
        assertEquals(createdUser.getFirstName(), "Harry");
    }

    @Test
    public void user_test_7_Given_UserExistByUsername_When_createUser_Then_InvalidParameterException() {
        when(userRepositoryMock.findByUsernameOrEmail(anyString(), anyString())).thenReturn(user1);

        InvalidParameterException thrown = assertThrows(InvalidParameterException.class, ()->
                userService.createUser(newUser3));
        assertEquals("User with given Username already exists", thrown.getMessage());
    }

    @Test
    public void user_test_8_Given_UserExistByEmail_When_createUser_Then_InvalidParameterException() {
        when(userRepositoryMock.findByUsernameOrEmail(anyString(), anyString())).thenReturn(user1);

        InvalidParameterException thrown = assertThrows(InvalidParameterException.class, ()->
                userService.createUser(newUser4));
        assertEquals("User with given email already exists", thrown.getMessage());
    }
}