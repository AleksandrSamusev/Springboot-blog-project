package dev.practice.blogproject.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.blogproject.controllers._public.PublicUserController;
import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.dtos.comment.CommentShortDto;
import dev.practice.blogproject.dtos.message.MessageFullDto;
import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserNewDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.models.Role;
import dev.practice.blogproject.services.impl.UserServiceImpl;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.HashSet;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PublicUserController.class)
public class PublicUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void user_test_17_CreateUserTest() throws Exception {

        UserNewDto dto = new UserNewDto("John", "Doe",
                "johnDoe", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), "Hi! I'm John");

        UserFullDto result = new UserFullDto(1L, "John", "Doe",
                "johnDoe", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), Role.USER,
                "Hi! I'm John", false, new HashSet<MessageFullDto>(), new HashSet<MessageFullDto>(),
                new HashSet<ArticleShortDto>(), new HashSet<CommentShortDto>());

        Mockito.when(userService.createUser(dto)).thenReturn(result);

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void user_test_18_CreateUserTestThrowsInvalidParameterException() throws Exception {

        UserNewDto dto = new UserNewDto(null, "Doe",
                "johnDoe", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), "Hi! I'm John");

        UserFullDto result = new UserFullDto(1L, "John", "Doe",
                "johnDoe", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), Role.USER,
                "Hi! I'm John", false, new HashSet<>(), new HashSet<>(),
                new HashSet<>(), new HashSet<>());

        Mockito.when(userService.createUser(dto)).thenThrow(InvalidParameterException.class);

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    public void user_test_19_GetUserByIdTest() throws Exception {

        UserShortDto result = new UserShortDto(1L, "JohnDoe");

        Mockito.when(userService.getUserById(anyLong())).thenReturn(result);

        mockMvc.perform(get("/api/v1/public/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.username").value("JohnDoe"));
    }

    @Test
    public void user_test_20_GetUserByIdTestThrowsResourceNotFoundException() throws Exception {

        Mockito.when(userService.getUserById(anyLong())).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/api/v1/public/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void user_test_21_GetAllUsersTest() throws Exception {

        UserShortDto dto1 = new UserShortDto(1L, "user1");
        UserShortDto dto2 = new UserShortDto(2L, "user2");

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/v1/public/users"))
                .andDo(print())
                .andExpect(jsonPath("$[0].userId").value(dto1.getUserId()))
                .andExpect(jsonPath("$[0].username").value(dto1.getUsername()))
                .andExpect(jsonPath("$[1].userId").value(dto2.getUserId()))
                .andExpect(jsonPath("$[1].username").value(dto2.getUsername()))
                .andExpect(status().isOk());
    }


}
