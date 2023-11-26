package dev.practice.mainApp.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.controllers._public.UserPublicController;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.security.JWTTokenProvider;
import dev.practice.mainApp.services.UserService;
import dev.practice.mainApp.utils.Validations;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserPublicController.class)
public class UserPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private Validations validations;
    @MockBean
    private JWTTokenProvider jwtTokenProvider;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @WithMockUser
    public void user_test_19_GetUserByIdTest() throws Exception {

        UserShortDto result = new UserShortDto(1L, "JohnDoe");

        Mockito.when(userService.getUserById(anyLong())).thenReturn(result);

        mockMvc.perform(get("/api/v1/public/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
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
                .andDo(print());
    }
}
