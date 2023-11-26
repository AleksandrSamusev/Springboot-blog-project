package dev.practice.mainApp.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.config.SecurityConfig;
import dev.practice.mainApp.controllers._admin.TagAdminController;
import dev.practice.mainApp.repositories.RoleRepository;
import dev.practice.mainApp.security.JWTAuthenticationEntryPoint;
import dev.practice.mainApp.security.JWTTokenProvider;
import dev.practice.mainApp.services.TagService;
import dev.practice.mainApp.services.UserService;
import dev.practice.mainApp.utils.Validations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagAdminController.class)
@Import(SecurityConfig.class)
public class TagAdminControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private Validations validations;
    @MockBean
    private JWTTokenProvider jwtTokenProvider;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    TagService tagService;
    @MockBean
    JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void tag_test19_DeleteTagTest() throws Exception {
        doNothing().when(tagService).deleteTag(anyLong(), anyString());
        mockMvc.perform(delete("/api/v1/admin/tags/1")
                        .header("X-Current-User-Id", 1))
                .andExpect(status().isOk());
    }
}
