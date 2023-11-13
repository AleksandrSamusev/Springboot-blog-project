package dev.practice.blogproject.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.blogproject.controllers._public.PublicUserController;
import dev.practice.blogproject.dtos.article.ArticleShortDto;
import dev.practice.blogproject.dtos.comment.CommentShortDto;
import dev.practice.blogproject.dtos.message.MessageFullDto;
import dev.practice.blogproject.dtos.user.UserFullDto;
import dev.practice.blogproject.dtos.user.UserNewDto;
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
import java.time.LocalDate;
import java.util.HashSet;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(PublicUserController.class)
public class UserControllerTest {

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


}
