package dev.practice.blogproject.user;

import dev.practice.blogproject.controllers._private.PrivateUserController;
import dev.practice.blogproject.dtos.user.UserUpdateDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.models.Role;
import dev.practice.blogproject.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.HashSet;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateUserController.class)
public class PrivateUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void user_test_22_GetUserByIdTest() throws Exception {

        UserFullDto fullDto = new UserFullDto(1L, "John", "Doe",
                "johnDoe", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), Role.USER,
                "Hi! I'm John", false, new HashSet<>(), new HashSet<>(),
                new HashSet<>(), new HashSet<>());

        when(userService.getUserById(anyLong(), anyLong())).thenReturn(fullDto);

        mockMvc.perform(get("/api/v1/private/users/1")
                        .header("X-Current-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("johnDoe"));
    }

    @Test
    public void user_test_23_GetUserByIdTestThrowsResourceNotFoundException() throws Exception {

        when(userService.getUserById(anyLong(), anyLong())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/v1/private/users/1")
                        .header("X-Current-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void user_test_24_GetUserByIdTestThrowsActionForbiddenException() throws Exception {

        when(userService.getUserById(anyLong(), anyLong())).thenThrow(ActionForbiddenException.class);

        mockMvc.perform(get("/api/v1/private/users/1")
                        .header("X-Current-User-Id", 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    public void user_test_25_UpdateUserTest() throws Exception {

        UserFullDto fullDto = new UserFullDto(1L, "newFirstName", "newLastName",
                "newUsername", "newEmail@test.test",
                LocalDate.of(1990, 12, 12), Role.USER,
                "new about", false, new HashSet<>(), new HashSet<>(),
                new HashSet<>(), new HashSet<>());

        UserUpdateDto updateDto = new UserUpdateDto("newFirstName", "newLastName",
                "newUsername", "newEmail@test.test", LocalDate.of(1990, 12, 12),
                "new about");

        when(userService.updateUser(1L, 1L, updateDto)).thenReturn(fullDto);

        mockMvc.perform(patch("/api/v1/private/users/1")
                        .header("X-Current-User-Id", 1)
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void user_test_26_UpdateUserTestThrowsResourceNotFoundException() throws Exception {

        UserUpdateDto updateDto = new UserUpdateDto("newFirstName", "newLastName",
                "newUsername", "newEmail@test.test", LocalDate.of(1990, 12, 12),
                "new about");

        when(userService.updateUser(anyLong(), anyLong(), any())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/v1/private/users/1")
                        .header("X-Current-User-Id", 1)
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void user_test_27_UpdateUserTestThrowsActionForbiddenException() throws Exception {

        UserUpdateDto updateDto = new UserUpdateDto("newFirstName", "newLastName",
                "newUsername", "newEmail@test.test", LocalDate.of(1990, 12, 12),
                "new about");

        when(userService.updateUser(anyLong(), anyLong(), any())).thenThrow(ActionForbiddenException.class);

        mockMvc.perform(patch("/api/v1/private/users/1")
                        .header("X-Current-User-Id", 1)
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void user_test_28_DeleteUserTest() throws Exception {

        doNothing().when(userService).deleteUser(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/private/users/1")
                        .header("X-Current-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    public void user_test_28_DeleteUserTestThrowsActionForbiddenException() throws Exception {

        doThrow(ActionForbiddenException.class).when(userService).deleteUser(anyLong(), anyLong());
        mockMvc.perform(delete("/api/v1/private/users/1")
                        .header("X-Current-User-Id", 2))
                .andExpect(status().isForbidden());
    }

    @Test
    public void user_test_29_DeleteUserTestThrowsResourceNotFoundException() throws Exception {

        doThrow(ResourceNotFoundException.class).when(userService).deleteUser(anyLong(), anyLong());
        mockMvc.perform(delete("/api/v1/private/users/1")
                        .header("X-Current-User-Id", 2))
                .andExpect(status().isNotFound());
    }
}
