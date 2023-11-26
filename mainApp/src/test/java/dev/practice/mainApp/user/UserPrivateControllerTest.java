/*
package dev.practice.mainApp.user;

import dev.practice.mainApp.controllers._private.UserPrivateController;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.security.JWTAuthenticationFilter;
import dev.practice.mainApp.services.impl.UserServiceImpl;
import dev.practice.mainApp.utils.Validations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

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
*/
