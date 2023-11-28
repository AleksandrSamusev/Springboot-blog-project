package dev.practice.mainApp.user;

import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserNewDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.dtos.user.UserUpdateDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.impl.AuthServiceImpl;
import dev.practice.mainApp.services.impl.UserServiceImpl;
import dev.practice.mainApp.utils.Validations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    private UserRepository userRepositoryMock;
    @Mock
    private RoleRepository roleRepositoryMock;
    @Mock
    private Validations validations;
    @InjectMocks
    private UserServiceImpl userService;
    @InjectMocks
    private AuthServiceImpl authService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;


    private final Role roleUser = new Role(2L, "ROLE_USER");

    private final User user1 = new User(1L, "John", "Doe",
            "johnDoe", "password", "johnDoe@test.test",
            LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());


    private final User user2 = new User(2L, "Marry", "Dawson",
            "marryDawson", "password", "merryDawson@test.test",
            LocalDate.of(1995, 6, 14), new HashSet<>(), "Hi! I'm Marry", true,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final User user3 = new User(3L, "Harry", "Potter",
            "harryPotter", "password", "harryPotter@test.test",
            LocalDate.of(1901, 5, 13), new HashSet<>(), "Hi! I'm Harry", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final UserNewDto newUser3 = new UserNewDto("Harry", "Potter",
            "harryPotter", "password", "harryPotter@test.test",
            LocalDate.of(1901, 5, 13), "Hi! I'm Harry");

    private final UserNewDto newUser4 = new UserNewDto("Harry", "Potter",
            "Kirk", "password", "johnDoe@test.test",
            LocalDate.of(1901, 5, 13), "Hi! I'm Harry");

    private final UserUpdateDto updateUser5 = new UserUpdateDto("Martin", "Potter",
            "Kirk", "password", "johnDoe@test.test",
            LocalDate.of(1901, 5, 13), "Hi! I'm Harry");


    User admin = new User(1L, "Sam", "Samson", "samSamson",
            "password", "samSamson@test.test", LocalDate.of(1980, 1, 1),
            new HashSet<>(), "Hi! I'm Sam", false, new HashSet<Message>(), new HashSet<Message>(),
            new HashSet<Article>(), new HashSet<Comment>());

    User noAdmin = new User(2L, "Martin", "Potter", "Kirk123123123",
            "password", "johnDoe@test.test", LocalDate.of(2000, 12, 27),
            new HashSet<>(), "Hi! I'm John", true, new HashSet<Message>(), new HashSet<Message>(),
            new HashSet<Article>(), new HashSet<Comment>());

    User noAdminUnbanned = new User(2L, "Martin", "Potter", "Kirk123123123",
            "password", "johnDoe@test.test", LocalDate.of(2000, 12, 27),
            new HashSet<>(), "Hi! I'm John", false, new HashSet<Message>(), new HashSet<Message>(),
            new HashSet<Article>(), new HashSet<Comment>());

    User noAdminBanned = new User(2L, "Martin", "Potter", "Kirk123123123",
            "password", "johnDoe@test.test", LocalDate.of(2000, 12, 27),
            new HashSet<>(), "Hi! I'm John", true, new HashSet<Message>(), new HashSet<Message>(),
            new HashSet<Article>(), new HashSet<Comment>());


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

        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenReturn(user1);


        assertEquals(userService.getUserById(1L, "johnDoe").getClass(), UserFullDto.class);
        assertEquals(userService.getUserById(1L, "marryDawson").getClass(), UserFullDto.class);
    }

    @Test
    public void user_test_5_Given_UserWithRoleAsUser_When_getUserById_Then_ActionForbiddenException() {
        when(validations.checkUserExist(1L)).thenReturn(user1);

        ActionForbiddenException thrown = assertThrows(ActionForbiddenException.class, () ->
                userService.getUserById(1L, user3.getUsername()));
        assertEquals("Action forbidden for current user", thrown.getMessage());
    }

    @Test
    public void user_test_6_Given_ValidUser_When_createUser_Then_userCreated() {
        when(validations.usernameAlreadyExists(anyString())).thenReturn(false);
        when(validations.isExistsByEmail(anyString())).thenReturn(false);
        when(roleRepositoryMock.findByName(any())).thenReturn(Optional.of(roleUser));
        when(userRepositoryMock.save(any())).thenReturn(user3);

        authService.register(newUser3);

        assertEquals(newUser3.getUsername(), user3.getUsername());
        assertEquals(newUser3.getEmail(), user3.getEmail());
        assertEquals(newUser3.getFirstName(), user3.getFirstName());
        assertEquals(newUser3.getLastName(), user3.getLastName());
    }

    @Test
    public void user_test_7_Given_UserExistByUsername_When_createUser_Then_InvalidParameterException() {
        when(validations.usernameAlreadyExists(anyString())).thenReturn(true);


        InvalidParameterException thrown = assertThrows(InvalidParameterException.class, () ->
                authService.register(newUser3));
        assertEquals("User with given username = 'harryPotter' already exists", thrown.getMessage());
    }

    @Test
    public void user_test_8_Given_UserExistByEmail_When_createUser_Then_InvalidParameterException() {
        when(validations.isExistsByEmail(anyString())).thenReturn(true);


        InvalidParameterException thrown = assertThrows(InvalidParameterException.class, () ->
                authService.register(newUser4));
        assertEquals("User with given email = 'johnDoe@test.test' already exists", thrown.getMessage());
    }

    @Test
    public void user_test_9_Given_ValidDtoAndIds_When_updateUser_Then_userUpdated() {

        User user = new User(1L, "John", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        UserUpdateDto updateDto = new UserUpdateDto("UPDATED FIRST NAME", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(1901, 5, 13), "Hi! I'm John");

        User updatedUser = new User(1L, "UPDATED FIRST NAME", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenReturn(user);
        when(validations.checkUserExist(anyLong())).thenReturn(user);
        when(validations.usernameAlreadyExists(anyString())).thenReturn(false);
        when(userRepositoryMock.save(any())).thenReturn(updatedUser);

        UserFullDto dto = userService.updateUser(1L, updateDto, "johnDoe");

        assertEquals(dto.getUsername(), updateDto.getUsername());
    }

    @Test
    public void user_test_10_Given_userIdNotExistsInDb_When_updateUser_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "User with id 8 wasn't found")).when(validations).checkUserExist(8L);

        assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUser(8L, updateUser5, "johnDoe"));
    }

    @Test
    public void user_test_11_Given_currentUserIdNotExistsInDb_When_updateUser_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "User with id 1 wasn't found")).when(validations).checkUserExist(1L);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                userService.updateUser(1L, updateUser5, "xxx"));
        assertEquals("User with id 1 wasn't found", exception.getMessage());
    }

    @Test
    public void user_test_12_Given_currentUserIdNotEqualUserId_When_updateUser_Then_ActionForbiddenException() {

        User user = new User(1L, "John", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        User user2 = new User(2L, "Marry", "Whatson",
                "marryWhatson", "password", "merryWhatson@test.test",
                LocalDate.of(1981, 6, 6), new HashSet<>(), "Hi! I'm Marry", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        UserUpdateDto updateDto = new UserUpdateDto("UPDATED FIRST NAME", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(1901, 5, 13), "Hi! I'm John");

        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenReturn(user);
        when(validations.checkUserExist(anyLong())).thenReturn(user2);
        when(validations.isAdmin(anyString())).thenReturn(false);


        ActionForbiddenException exception = assertThrows(ActionForbiddenException.class, () ->
                userService.updateUser(2L, updateDto, "marryWhatson"));
        assertEquals("Action forbidden for current user", exception.getMessage());
    }

    @Test
    public void user_test_13_Given_validUserId_When_deleteUser_Then_userDeleted() {

        User user = new User(1L, "John", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenReturn(user);
        when(validations.checkUserExist(anyLong())).thenReturn(user);

        Mockito.doNothing().when(userRepositoryMock).deleteById(1L);

        userService.deleteUser(1L, "johnDoe");

        verify(userRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    public void user_test_14_Given_userIdNotEqualsCurrentUserId_When_deleteUser_Then_ActionForbiddenException() {

        User user = new User(1L, "John", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        User user2 = new User(2L, "Marry", "Whatson",
                "marryWhatson", "password", "merryWhatson@test.test",
                LocalDate.of(1981, 6, 6), new HashSet<>(), "Hi! I'm Marry", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenReturn(user);
        when(validations.checkUserExist(anyLong())).thenReturn(user2);
        when(validations.isAdmin(anyString())).thenReturn(false);

        ActionForbiddenException thrown = assertThrows(ActionForbiddenException.class, () ->
                userService.deleteUser(2L, "marryWhatson"));
        assertEquals("Action forbidden for current user", thrown.getMessage());
    }

    @Test
    public void user_test_15_Given_userIdNotExist_When_deleteUser_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "User with id 2 wasn't found")).when(validations).checkUserExist(2L);

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                userService.deleteUser(2L, "johnDoe"));
        assertEquals("User with id 2 wasn't found", thrown.getMessage());
    }

    @Test
    public void user_test_16_Given_currentUserIdNotExist_When_deleteUser_Then_ResourceNotFoundException() {
        doThrow(new ResourceNotFoundException(
                "User with id 1 wasn't found")).when(validations).checkUserExist(1L);

        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () ->
                userService.deleteUser(1L, "marryDawson"));
        assertEquals("User with id 1 wasn't found", thrown.getMessage());
    }

    @Test
    public void user_test_45_Given_UsernameWithWhitespaces_When_createUser_Then_userCreated() {
        UserNewDto dto = new UserNewDto("Harry", "Potter",
                "harry      Potter", "password", "harryPotter@test.test",
                LocalDate.of(1901, 5, 13), "Hi! I'm Harry");

        User user = new User(1L, "Harry", "Potter", "harryPotter",
                "password", "harryPotter@test.test", LocalDate.of(1901, 5,
                13), new HashSet<>(), "Hi! I'm Harry", false, new HashSet<Message>(),
                new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when((validations.usernameAlreadyExists(anyString()))).thenReturn(false);
        when(validations.isExistsByEmail(anyString())).thenReturn(false);
        when(roleRepositoryMock.findByName(any())).thenReturn(Optional.of(roleUser));
        when(userRepositoryMock.save(any())).thenReturn(user);
        when(userRepositoryMock.findByUsername(anyString())).thenReturn(user);

        authService.register(dto);

        User findUser = userRepositoryMock.findByUsername("harryPotter");

        assertEquals(findUser.getUsername(), "harryPotter");
    }


    @Test
    public void user_test_46_Given_UsernameWithWhitespaces_When_updateUser_Then_userUpdated() {
        User user = new User(1L, "John", "Doe",
                "john     Doe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        UserUpdateDto updateDto = new UserUpdateDto("UPDATED FIRST NAME", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(1901, 5, 13), "Hi! I'm John");

        User updatedUser = new User(1L, "UPDATED FIRST NAME", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenReturn(user);
        when(validations.checkUserExist(anyLong())).thenReturn(user);
        when(validations.usernameAlreadyExists(anyString())).thenReturn(false);
        when(userRepositoryMock.save(any())).thenReturn(updatedUser);

        UserFullDto dto = userService.updateUser(1L, updateDto, "johnDoe");

        assertEquals(dto.getUsername(), "johnDoe");
    }

    @Test
    public void user_test_47_Given_ValidIds_When_banUser_Then_userBanned() {

        User user = new User(1L, "John", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        User user2 = new User(2L, "Marry", "Whatson",
                "marryWhatson", "password", "merryWhatson@test.test",
                LocalDate.of(1981, 6, 6), new HashSet<>(), "Hi! I'm Marry", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        User user2banned = new User(2L, "Marry", "Whatson",
                "marryWhatson", "password", "merryWhatson@test.test",
                LocalDate.of(1981, 6, 6), new HashSet<>(), "Hi! I'm Marry", true,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when(validations.checkUserExist(anyLong())).thenReturn(user);
        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenReturn(user2);
        when(validations.isAdmin(anyString())).thenReturn(true);
        when(userRepositoryMock.save(any())).thenReturn(user2banned);

        UserFullDto result = userService.banUser(2L, "johnDoe");

        assertEquals(result.getUserId(), user2.getUserId());
        assertEquals(result.getFirstName(), user2.getFirstName());
        assertEquals(result.getLastName(), user2.getLastName());
        assertEquals(result.getIsBanned(), Boolean.TRUE);
    }

    @Test
    public void user_test_48_Given_ValidIds_When_unbanUser_Then_userUnbanned() {
        User user = new User(1L, "John", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        User user2 = new User(2L, "Marry", "Whatson",
                "marryWhatson", "password", "merryWhatson@test.test",
                LocalDate.of(1981, 6, 6), new HashSet<>(), "Hi! I'm Marry", true,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        User user2unbanned = new User(2L, "Marry", "Whatson",
                "marryWhatson", "password", "merryWhatson@test.test",
                LocalDate.of(1981, 6, 6), new HashSet<>(), "Hi! I'm Marry", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when(validations.checkUserExist(anyLong())).thenReturn(user);
        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenReturn(user2);
        when(validations.isAdmin(anyString())).thenReturn(true);
        when(userRepositoryMock.save(any())).thenReturn(user2unbanned);

        UserFullDto result = userService.unbanUser(2L, "johnDoe");

        assertEquals(result.getUserId(), user2.getUserId());
        assertEquals(result.getFirstName(), user2.getFirstName());
        assertEquals(result.getLastName(), user2.getLastName());
        assertEquals(result.getIsBanned(), Boolean.FALSE);
    }

    @Test
    public void user_test_49_Given_userNotExists_When_banUser_Then_ResourceNotFound() {

        doThrow(new ResourceNotFoundException(
                "User with id 2 wasn't found")).when(validations).checkUserExist(2L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                userService.banUser(2L, "marryWhatson"));
        assertEquals("User with id 2 wasn't found", ex.getMessage());
    }

    @Test
    public void user_test_50_Given_currentUserNotExists_When_banUser_Then_ResourceNotFound() {
        User user2 = new User(2L, "Marry", "Whatson",
                "marryWhatson", "password", "merryWhatson@test.test",
                LocalDate.of(1981, 6, 6), new HashSet<>(), "Hi! I'm Marry", true,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when(validations.checkUserExist(2L)).thenReturn(user2);
        when(validations.checkUserExistsByUsernameOrEmail(anyString()))
                .thenThrow(new ResourceNotFoundException("User with given credentials not found"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                userService.banUser(2L, "johnDoe"));
        assertEquals("User with given credentials not found", ex.getMessage());
    }

    @Test
    public void user_test_51_Given_currentUserNotAdmin_When_banUser_Then_ActionForbidden() {

        User user = new User(1L, "John", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        User user2 = new User(2L, "Marry", "Whatson",
                "marryWhatson", "password", "merryWhatson@test.test",
                LocalDate.of(1981, 6, 6), new HashSet<>(), "Hi! I'm Marry", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when(validations.checkUserExist(anyLong())).thenReturn(user);
        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenReturn(user2);
        when(validations.isAdmin("johnDoe")).thenReturn(false);

        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                userService.banUser(2L, "johnDoe"));
        assertEquals("Action forbidden for current user", ex.getMessage());
    }

    @Test
    public void user_test_52_Given_userNotExists_When_unbanUser_Then_ResourceNotFound() {
        doThrow(new ResourceNotFoundException(
                "User with id 2 wasn't found")).when(validations).checkUserExist(2L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                userService.unbanUser(2L, "marryWhatson"));
        assertEquals("User with id 2 wasn't found", ex.getMessage());
    }

    @Test
    public void user_test_53_Given_currentUserNotExists_When_unbanUser_Then_ResourceNotFound() {
        User user2 = new User(2L, "Marry", "Whatson",
                "marryWhatson", "password", "merryWhatson@test.test",
                LocalDate.of(1981, 6, 6), new HashSet<>(), "Hi! I'm Marry", true,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when(validations.checkUserExist(2L)).thenReturn(user2);
        when(validations.checkUserExistsByUsernameOrEmail(anyString()))
                .thenThrow(new ResourceNotFoundException("User with given credentials not found"));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                userService.unbanUser(2L, "johnDoe"));
        assertEquals("User with given credentials not found", ex.getMessage());
    }

    @Test
    public void user_test_54_Given_currentUserNotAdmin_When_unbanUser_Then_ActionForbidden() {
        User user = new User(1L, "John", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        User user2 = new User(2L, "Marry", "Whatson",
                "marryWhatson", "password", "merryWhatson@test.test",
                LocalDate.of(1981, 6, 6), new HashSet<>(), "Hi! I'm Marry", true,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        when(validations.checkUserExist(anyLong())).thenReturn(user);
        when(validations.checkUserExistsByUsernameOrEmail(anyString())).thenReturn(user2);
        when(validations.isAdmin(anyString())).thenReturn(false);

        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                userService.unbanUser(2L, "johnDoe"));
        assertEquals("Action forbidden for current user", ex.getMessage());
    }
}