package dev.practice.mainApp.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.controllers._admin.UserAdminController;
import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.Role;
import dev.practice.mainApp.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAdminController.class)
public class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper mapper;

    private final UserFullDto bannedUser = new UserFullDto(1L, "John", "Doe",
            "johnDoe", "johnDoe@test.test",
            LocalDate.of(2000, 12, 27), Role.USER,
            "Hi! I'm John", true, new HashSet<>(), new HashSet<>(),
            new HashSet<>(), new HashSet<>());

    private final UserFullDto notBannedUser = new UserFullDto(1L, "John", "Doe",
            "johnDoe", "johnDoe@test.test",
            LocalDate.of(2000, 12, 27), Role.USER,
            "Hi! I'm John", false, new HashSet<>(), new HashSet<>(),
            new HashSet<>(), new HashSet<>());

    @Test
    public void user_test55_Given_ValidIds_When_banUser_200_OK() throws Exception {

        when(userService.banUser(anyLong(), anyLong())).thenReturn(bannedUser);

        mockMvc.perform(patch("/api/v1/admin/users/{userId}/ban", bannedUser.getUserId())
                        .header("X-Current-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void user_test56_Given_UserNotExists_When_banUser_404_NOT_FOUND() throws Exception {

        when(userService.banUser(anyLong(), anyLong())).thenThrow(
                new ResourceNotFoundException("User with given ID = 1 not found"));

        mockMvc.perform(patch("/api/v1/admin/users/{userId}/ban", bannedUser.getUserId())
                        .header("X-Current-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void user_test57_Given_CurrentNotAdmin_When_banUser_403_FORBIDDEN() throws Exception {

        when(userService.banUser(anyLong(), anyLong())).thenThrow(
                new ActionForbiddenException("Action forbidden for current user"));

        mockMvc.perform(patch("/api/v1/admin/users/{userId}/ban", bannedUser.getUserId())
                        .header("X-Current-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void user_test58_Given_ValidIds_When_unbanUser_200_OK() throws Exception {

        when(userService.unbanUser(anyLong(), anyLong())).thenReturn(notBannedUser);

        mockMvc.perform(patch("/api/v1/admin/users/{userId}/unban", notBannedUser.getUserId())
                        .header("X-Current-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void user_test59_Given_UserNotExists_When_unbanUser_404_NOT_FOUND() throws Exception {

        when(userService.unbanUser(anyLong(), anyLong())).thenThrow(
                new ResourceNotFoundException("User with given ID = 1 not found"));

        mockMvc.perform(patch("/api/v1/admin/users/{userId}/unban", notBannedUser.getUserId())
                        .header("X-Current-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void user_test60_Given_CurrentNotAdmin_When_unbanUser_403_FORBIDDEN() throws Exception {

        when(userService.unbanUser(anyLong(), anyLong())).thenThrow(
                new ActionForbiddenException("Action forbidden for current user"));

        mockMvc.perform(patch("/api/v1/admin/users/{userId}/unban", notBannedUser.getUserId())
                        .header("X-Current-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
