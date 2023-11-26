package dev.practice.mainApp.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.controllers._private.UserPrivateController;
import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserUpdateDto;
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

@WebMvcTest(UserPrivateController.class)
public class UserPrivateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void user_test_22_GetUserByIdTest() throws Exception {

        UserFullDto fullDto = new UserFullDto(1L, "John", "Doe",
                "johnDoe","password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(),
                "Hi! I'm John", false, new HashSet<>(), new HashSet<>(),
                new HashSet<>(), new HashSet<>());

        when(userService.getUserById(anyLong(), anyString())).thenReturn(fullDto);

        mockMvc.perform(get("/api/v1/private/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("johnDoe"));
    }

    @Test
    public void user_test_23_GetUserByIdTestThrowsResourceNotFoundException() throws Exception {

        when(userService.getUserById(anyLong(), anyString())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/v1/private/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    public void user_test_24_GetUserByIdTestThrowsActionForbiddenException() throws Exception {

        when(userService.getUserById(anyLong(), anyString())).thenThrow(ActionForbiddenException.class);

        mockMvc.perform(get("/api/v1/private/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    public void user_test_25_UpdateUserTest() throws Exception {

        UserFullDto fullDto = new UserFullDto(1L, "newFirstName", "newLastName",
                "newUsername","password", "newEmail@test.test",
                LocalDate.of(1990, 12, 12), new HashSet<>(),
                "new about", false, new HashSet<>(), new HashSet<>(),
                new HashSet<>(), new HashSet<>());

        UserUpdateDto updateDto = new UserUpdateDto("newFirstName", "newLastName",
                "newUsername", "password", "newEmail@test.test", LocalDate.of(1990, 12, 12),
                "new about");

        when(userService.updateUser(1L, updateDto, "newUsername")).thenReturn(fullDto);

        mockMvc.perform(patch("/api/v1/private/users/1")
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void user_test_26_UpdateUserTestThrowsResourceNotFoundException() throws Exception {

        UserUpdateDto updateDto = new UserUpdateDto("newFirstName", "newLastName",
                "newUsername","password", "newEmail@test.test", LocalDate.of(1990, 12, 12),
                "new about");

        when(userService.updateUser(anyLong(), any(), anyString())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/api/v1/private/users/1")
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void user_test_27_UpdateUserTestThrowsActionForbiddenException() throws Exception {

        UserUpdateDto updateDto = new UserUpdateDto("newFirstName", "newLastName",
                "newUsername", "password", "newEmail@test.test", LocalDate.of(1990, 12, 12),
                "new about");

        when(userService.updateUser(anyLong(), any(), anyString())).thenThrow(ActionForbiddenException.class);

        mockMvc.perform(patch("/api/v1/private/users/1")
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void user_test_28_DeleteUserTest() throws Exception {

        doNothing().when(userService).deleteUser(anyLong(), anyString());

        mockMvc.perform(delete("/api/v1/private/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void user_test_28_DeleteUserTestThrowsActionForbiddenException() throws Exception {

        doThrow(ActionForbiddenException.class).when(userService).deleteUser(anyLong(), anyString());
        mockMvc.perform(delete("/api/v1/private/users/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void user_test_29_DeleteUserTestThrowsResourceNotFoundException() throws Exception {

        doThrow(ResourceNotFoundException.class).when(userService).deleteUser(anyLong(), anyString());
        mockMvc.perform(delete("/api/v1/private/users/1"))
                .andExpect(status().isNotFound());
    }
}
