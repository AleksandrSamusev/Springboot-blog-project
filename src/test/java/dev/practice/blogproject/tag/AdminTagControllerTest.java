package dev.practice.blogproject.tag;

import dev.practice.blogproject.controllers._admin.AdminTagController;
import dev.practice.blogproject.services.impl.TagServiceImpl;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminTagController.class)
public class AdminTagControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagServiceImpl tagService;

    @Test
    public void tag_test19_DeleteTagTest() throws Exception{
        doNothing().when(tagService).deleteTag(anyLong(), anyLong());
        mockMvc.perform(delete("/api/v1/admin/tags/1")
                        .header("X-Current-User-Id", 1))
                .andExpect(status().isOk());
    }
}
