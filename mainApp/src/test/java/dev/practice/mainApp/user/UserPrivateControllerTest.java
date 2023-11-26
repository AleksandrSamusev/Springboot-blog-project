package dev.practice.mainApp.user;

import dev.practice.mainApp.controllers._private.UserPrivateController;
import dev.practice.mainApp.controllers._public.UserPublicController;
import dev.practice.mainApp.dtos.user.UserFullDto;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.security.JWTAuthenticationFilter;
import dev.practice.mainApp.security.JWTTokenProvider;
import dev.practice.mainApp.services.impl.UserServiceImpl;
import dev.practice.mainApp.utils.Validations;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserPrivateController.class)
public class UserPrivateControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private Validations validations;
    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;


    @Test
    @WithMockUser(roles = "ADMIN")
    public void user_test_22_GetUserByIdTest() throws Exception {

        mockMvc.perform(get("/api/v1/private/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
