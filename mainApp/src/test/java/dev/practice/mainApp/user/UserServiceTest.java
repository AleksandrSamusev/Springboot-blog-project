package dev.practice.mainApp.user;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserNewDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.dtos.user.UserUpdateDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.impl.UserServiceImpl;
import dev.practice.mainApp.utils.Validations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private Validations validations;
    @InjectMocks
    private UserServiceImpl userService;

    /*private final User user1 = new User(1L, "John", "Doe",
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

    private final UserUpdateDto updateUser5 = new UserUpdateDto("Martin", "Potter",
            "Kirk", "johnDoe@test.test",
            LocalDate.of(1901, 5, 13), "Hi! I'm Harry");


    User admin = new User(1L, "Sam", "Samson",
            "samSamson", "samSamson@test.test",
            LocalDate.of(1980, 1, 1), Role.ADMIN, "Hi! I'm Sam", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    User noAdmin = new User(2L, "Martin", "Potter",
            "Kirk123123123", "johnDoe@test.test",
            LocalDate.of(2000, 12, 27), Role.USER, "Hi! I'm John", true,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    User noAdminUnbanned = new User(2L, "Martin", "Potter",
            "Kirk123123123", "johnDoe@test.test",
            LocalDate.of(2000, 12, 27), Role.USER, "Hi! I'm John", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    User noAdminBanned = new User(2L, "Martin", "Potter",
            "Kirk123123123", "johnDoe@test.test",
            LocalDate.of(2000, 12, 27), Role.USER, "Hi! I'm John", true,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());


    @Test
    public void user_test_1_When_getAllUsers_Then_returnListOfAllUsers() {
        when(userRepositoryMock.findAll()).thenReturn(List.of(user1, user2));

        List<UserShortDto> result = userService.getAllUsers();

        assertEquals(result.get(0).getUsername(), user1.getUsername());
        assertEquals(result.get(1).getUsername(), user2.getUsername());
        verify(userRepositoryMock, times(1)).findAll();
    }

    @Test
    public void user_test_2_Given_ValidUserId_When_getUserById_Then_returnValidUser() {
        when(validations.checkUserExist(1L)).thenReturn(user1);
        when(validations.checkUserExist(2L)).thenReturn(user2);

        UserShortDto dto1 = userService.getUserById(1L);
        UserShortDto dto2 = userService.getUserById(2L);

        assertEquals(dto1.getUsername(), user1.getUsername());
        assertEquals(dto2.getUsername(), user2.getUsername());
    }

    @Test
    public void user_test_3_Given_InvalidUserId_When_getUserById_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "User with id 2 wasn't found")).when(validations).checkUserExist(2L);

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(2L);
        });
        assertEquals("User with id 2 wasn't found", thrown.getMessage());
    }

    @Test
    public void user_test_4_Given_ValidUserIdAndCurrentUserId_When_getUserById_Then_returnValidUser() {
        when(validations.checkUserExist(1L)).thenReturn(user1);
        when(validations.checkUserExist(2L)).thenReturn(user2);

        assertEquals(userService.getUserById(1L, 1L).getClass(), UserFullDto.class);
        assertEquals(userService.getUserById(1L, 2L).getClass(), UserFullDto.class);
    }

    @Test
    public void user_test_5_Given_UserWithRoleAsUser_When_getUserById_Then_ActionForbiddenException() {
        when(validations.checkUserExist(1L)).thenReturn(user1);
        when(validations.checkUserExist(3L)).thenReturn(user3);

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

        InvalidParameterException thrown = assertThrows(InvalidParameterException.class, () ->
                userService.createUser(newUser3));
        assertEquals("User with given Username already exists", thrown.getMessage());
    }

    @Test
    public void user_test_8_Given_UserExistByEmail_When_createUser_Then_InvalidParameterException() {
        when(userRepositoryMock.findByUsernameOrEmail(anyString(), anyString())).thenReturn(user1);

        InvalidParameterException thrown = assertThrows(InvalidParameterException.class, () ->
                userService.createUser(newUser4));
        assertEquals("User with given email already exists", thrown.getMessage());
    }

    @Test
    public void user_test_9_Given_ValidDtoAndIds_When_updateUser_Then_userUpdated() {
        when(validations.checkUserExist(any())).thenReturn(user1);
        when(userRepositoryMock.save(any())).thenReturn(user1);

        UserFullDto dto = userService.updateUser(1L, 1L, updateUser5);

        assertEquals(dto.getFirstName(), updateUser5.getFirstName());
    }

    @Test
    public void user_test_10_Given_userIdNotExistsInDb_When_updateUser_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "User with id 8 wasn't found")).when(validations).checkUserExist(8L);

        assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUser(8L, 1L, updateUser5));
    }

    @Test
    public void user_test_11_Given_currentUserIdNotExistsInDb_When_updateUser_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "User with id 1 wasn't found")).when(validations).checkUserExist(1L);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUser(1L, 8L, updateUser5));
        assertEquals("User with id 1 wasn't found", exception.getMessage());
    }

    @Test
    public void user_test_12_Given_currentUserIdNotEqualUserId_When_updateUser_Then_ActionForbiddenException() {
        when(validations.checkUserExist(1L)).thenReturn(user1);
        when(validations.checkUserExist(3L)).thenReturn(user3);

        ActionForbiddenException exception = assertThrows(ActionForbiddenException.class, () ->
                userService.updateUser(1L, 3L, updateUser5));

        assertEquals("Action forbidden for current user", exception.getMessage());
    }

    @Test
    public void user_test_13_Given_validUserId_When_deleteUser_Then_userDeleted() {
        when(validations.checkUserExist(1L)).thenReturn(user1);
        Mockito.doNothing().when(userRepositoryMock).deleteById(1L);

        userService.deleteUser(1L, 1L);

        verify(userRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    public void user_test_14_Given_userIdNotEqualsCurrentUserId_When_deleteUser_Then_ActionForbiddenException() {
        when(validations.checkUserExist(1L)).thenReturn(user1);
        when(validations.checkUserExist(3L)).thenReturn(user3);
        doThrow(new ActionForbiddenException(
                "Action forbidden for current user")).when(validations).isUserAuthorized(anyLong(), any());

        ActionForbiddenException thrown = assertThrows(ActionForbiddenException.class, () ->
                userService.deleteUser(1L, 3L));
        assertEquals("Action forbidden for current user", thrown.getMessage());
    }

    @Test
    public void user_test_15_Given_userIdNotExist_When_deleteUser_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "User with id 2 wasn't found")).when(validations).checkUserExist(2L);

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                userService.deleteUser(2L, 1L));
        assertEquals("User with id 2 wasn't found", thrown.getMessage());
    }

    @Test
    public void user_test_16_Given_currentUserIdNotExist_When_deleteUser_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "User with id 1 wasn't found")).when(validations).checkUserExist(1L);

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                userService.deleteUser(1L, 2L));
        assertEquals("User with id 1 wasn't found", thrown.getMessage());
    }

    @Test
    public void user_test_45_Given_UsernameWithWhitespaces_When_createUser_Then_userCreated() {
        UserNewDto newUser3 = new UserNewDto("Harry", "Potter",
                "harry      Potter", "harryPotter@test.test",
                LocalDate.of(1901, 5, 13), "Hi! I'm Harry");

        User user4 = new User(3L, "Harry", "Potter",
                "harryPotter", "harryPotter@test.test",
                LocalDate.of(1901, 5, 13), Role.USER, "Hi! I'm Harry", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when(userRepositoryMock.findByUsernameOrEmail(anyString(), anyString())).thenReturn(null);
        when(userRepositoryMock.save(any())).thenReturn(user4);

        UserFullDto createdUser = userService.createUser(newUser3);

        assertEquals(createdUser.getClass(), UserFullDto.class);
        assertEquals(createdUser.getUsername(), "harryPotter");
    }


    @Test
    public void user_test_46_Given_UsernameWithWhitespaces_When_updateUser_Then_userUpdated() {
        User user1 = new User(1L, "Martin", "Potter",
                "Kirk456456456", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), Role.USER, "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        UserUpdateDto updateUser5 = new UserUpdateDto("Martin", "Potter",
                "Kirk 123 123 123", "johnDoe@test.test",
                LocalDate.of(1901, 5, 13), "Hi! I'm Harry");

        when(validations.checkUserExist(any())).thenReturn(user1);
        when(userRepositoryMock.save(any())).thenReturn(user1);

        UserFullDto dto = userService.updateUser(1L, 1L, updateUser5);

        assertEquals(dto.getUsername(), "Kirk123123123");
    }

    @Test
    public void user_test_47_Given_ValidIds_When_banUser_Then_userBanned() {
        when(validations.checkUserExist(1L)).thenReturn(admin);
        when(validations.checkUserExist(2L)).thenReturn(noAdmin);
        when(userRepositoryMock.save(any())).thenReturn(noAdminBanned);

        UserFullDto result = userService.banUser(2L, 1L);

        assertEquals(result.getUserId(), noAdmin.getUserId());
        assertEquals(result.getFirstName(), noAdmin.getFirstName());
        assertEquals(result.getLastName(), noAdmin.getLastName());
        assertEquals(result.getIsBanned(), Boolean.TRUE);
    }

    @Test
    public void user_test_48_Given_ValidIds_When_unbanUser_Then_userBanned() {
        when(validations.checkUserExist(1L)).thenReturn(admin);
        when(validations.checkUserExist(2L)).thenReturn(noAdminBanned);
        when(userRepositoryMock.save(any())).thenReturn(noAdminUnbanned);

        UserFullDto result = userService.unbanUser(2L, 1L);

        assertEquals(result.getUserId(), noAdmin.getUserId());
        assertEquals(result.getFirstName(), noAdmin.getFirstName());
        assertEquals(result.getLastName(), noAdmin.getLastName());
        assertEquals(result.getIsBanned(), Boolean.FALSE);
    }

    @Test
    public void user_test_49_Given_userNotExists_When_banUser_Then_ResourceNotFound() {
        doThrow(new ResourceNotFoundException(
                "User with id 2 wasn't found")).when(validations).checkUserExist(2L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                userService.banUser(2L, 1L));
        assertEquals("User with id 2 wasn't found", ex.getMessage());
    }

    @Test
    public void user_test_50_Given_currentUserNotExists_When_banUser_Then_ResourceNotFound() {
        when(validations.checkUserExist(2L)).thenReturn(noAdmin);
        doThrow(new ResourceNotFoundException(
                "User with id 1 wasn't found")).when(validations).checkUserExist(1L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                userService.banUser(2L, 1L));
        assertEquals("User with id 1 wasn't found", ex.getMessage());
    }

    @Test
    public void user_test_51_Given_currentUserNotAdmin_When_banUser_Then_ActionForbidden() {
        when(validations.checkUserExist(2L)).thenReturn(noAdmin);
        doThrow(new ActionForbiddenException(
                "User with id 1 is not ADMIN. Access is forbidden")).when(validations).checkUserIsAdmin(any());

        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                userService.banUser(2L, 1L));
        assertEquals("User with id 1 is not ADMIN. Access is forbidden", ex.getMessage());
    }

    @Test
    public void user_test_52_Given_userNotExists_When_unbanUser_Then_ResourceNotFound() {
        doThrow(new ResourceNotFoundException(
                "User with id 2 wasn't found")).when(validations).checkUserExist(2L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                userService.unbanUser(2L, 1L));
        assertEquals("User with id 2 wasn't found", ex.getMessage());
    }

    @Test
    public void user_test_53_Given_currentUserNotExists_When_unbanUser_Then_ResourceNotFound() {
        when(validations.checkUserExist(2L)).thenReturn(noAdmin);
        doThrow(new ResourceNotFoundException(
                "User with id 1 wasn't found")).when(validations).checkUserExist(1L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                userService.unbanUser(2L, 1L));
        assertEquals("User with id 1 wasn't found", ex.getMessage());
    }

    @Test
    public void user_test_54_Given_currentUserNotAdmin_When_unbanUser_Then_ActionForbidden() {
        when(validations.checkUserExist(2L)).thenReturn(noAdmin);
        doThrow(new ActionForbiddenException(
                "User with id 1 is not ADMIN. Access is forbidden")).when(validations).checkUserIsAdmin(any());

        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                userService.unbanUser(2L, 1L));
        assertEquals("User with id 1 is not ADMIN. Access is forbidden", ex.getMessage());
    }*/
}