package dev.practice.mainApp.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.controllers._public.UserPublicController;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.Comment;
import dev.practice.mainApp.models.Message;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.security.JWTAuthenticationFilter;
import dev.practice.mainApp.services.impl.UserServiceImpl;
import dev.practice.mainApp.utils.Validations;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserPublicController.class)
@AutoConfigureMockMvc
public class UserPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private Validations validations;

    @Test
    public void user_test_19_GetUserByIdTest() throws Exception {

        User user = new User(1L, "John", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), new HashSet<>(), "Hi! I'm John", false,
                new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

        UserShortDto result = new UserShortDto(1L, "JohnDoe");
        when(validations.checkUserExist(anyLong())).thenReturn(user);
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
