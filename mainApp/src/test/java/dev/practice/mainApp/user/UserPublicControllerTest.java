package dev.practice.mainApp.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.controllers._public.UserPublicController;
import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.dtos.user.UserNewDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.Role;
import dev.practice.mainApp.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserPublicController.class)
public class UserPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper mapper;

    private final UserFullDto result = new UserFullDto(1L, "John", "Doe",
            "johnDoe","password",  "johnDoe@test.test",
            LocalDate.of(2000, 12, 27), new HashSet<>(),
            "Hi! I'm John", false, new HashSet<>(), new HashSet<>(),
            new HashSet<>(), new HashSet<>());

    @Test
    public void user_test_17_CreateUserTest() throws Exception {

        UserNewDto dto = new UserNewDto("John", "Doe",
                "johnDoe","password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), "Hi! I'm John");

        mockMvc.perform(post("/api/v1/auth")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }


    @Test
    public void user_test_18_CreateUserTestThrowsInvalidParameterException() throws Exception {

        UserNewDto dto = new UserNewDto(null, "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), "Hi! I'm John");

        mockMvc.perform(post("/api/v1/auth")
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

    @Test
    public void user_test_35_Given_FirstNameIsNull_When_CreateUser_Then_BadRequest() throws Exception {

        UserNewDto dto = new UserNewDto(null, "Doe",
                "johnDoe","password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), "Hi! I'm John");

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("User first name cannot be blank")));
    }

    @Test
    public void user_test_36_Given_LastNameIsNull_When_CreateUser_Then_BadRequest() throws Exception {

        UserNewDto dto = new UserNewDto("John", null,
                "johnDoe","password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), "Hi! I'm John");

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("User last name cannot be blank")));
    }

    @Test
    public void user_test_37_Given_UsernameIsNull_When_CreateUser_Then_BadRequest() throws Exception {

        UserNewDto dto = new UserNewDto("John", "Doe",
                null, "password", "johnDoe@test.test",
                LocalDate.of(2000, 12, 27), "Hi! I'm John");

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Username cannot be blank")));
    }


    @Test
    public void user_test_37_Given_EmailIsNull_When_CreateUser_Then_BadRequest() throws Exception {

        UserNewDto dto = new UserNewDto("John", "Doe",
                "johnDoe","password", null,
                LocalDate.of(2000, 12, 27), "Hi! I'm John");

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("User email cannot be blank")));
    }


    @Test
    public void user_test_38_Given_EmailIsNotValid_When_CreateUser_Then_BadRequest() throws Exception {

        UserNewDto dto = new UserNewDto("John", "Doe",
                "johnDoe","password", "johnDoetest.test",
                LocalDate.of(2000, 12, 27), "Hi! I'm John");

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Incorrect email format")));
    }


    @Test
    public void user_test_39_Given_SeveralNullParams_When_CreateUser_Then_BadRequestAndListErrors() throws Exception {

        UserNewDto dto = new UserNewDto(null, null,
                null, null, null,
                LocalDate.of(2000, 12, 27), "Hi! I'm John");

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.size()", is(5)));
    }

    @Test
    public void user_test_40_Given_BirthDateInFuture_When_CreateUser_Then_BadRequest() throws Exception {

        UserNewDto dto = new UserNewDto("John", "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2025, 12, 27), "Hi! I'm John");

          mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Birth date should be in past")));
    }

    @Test
    public void user_test_41_Given_FirstNameLength52Chars_When_CreateUser_Then_BadRequest() throws Exception {

        UserNewDto dto = new UserNewDto("JohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohnJohn",
                "Doe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2021, 12, 27), "Hi! I'm John");

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Users first name should be 50 chars max")));
    }

    @Test
    public void user_test_42_Given_LastNameLength60Chars_When_CreateUser_Then_BadRequest() throws Exception {

        UserNewDto dto = new UserNewDto("John",
                "DoeDoeDoeDoeDoeDoeDoeDoeDoeDoeDoeDoeDoeDoeDoeDoeDoeDoeDoeDoe",
                "johnDoe", "password", "johnDoe@test.test",
                LocalDate.of(2021, 12, 27), "Hi! I'm John");

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Users last name should be 50 chars max")));
    }


    @Test
    public void user_test_43_Given_UsernameLength70Chars_When_CreateUser_Then_BadRequest() throws Exception {

        UserNewDto dto = new UserNewDto("John",
                "Doe",
                "johnDoejohnDoejohnDoejohnDoejohnDoejohnDoejohnDoejohnDoejohnDoejohnDoe", "password",
                "johnDoe@test.test",
                LocalDate.of(2021, 12, 27), "Hi! I'm John");

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Username should be 50 chars max")));
    }

    @Test
    public void user_test_44_Given_AboutLength1010Chars_When_CreateUser_Then_BadRequest() throws Exception {

        UserNewDto dto = new UserNewDto("John",
                "Doe",
                "johnDoe",
                "password",
                "johnDoe@test.test",
                LocalDate.of(2021, 12, 27),
                "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789" +
                        "01234567890123456789");

        mockMvc.perform(post("/api/v1/public/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("About should be 1000 char max")));
    }

}
