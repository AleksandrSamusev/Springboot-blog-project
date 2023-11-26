package dev.practice.mainApp.tag;

import dev.practice.mainApp.controllers._admin.TagAdminController;
import dev.practice.mainApp.services.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagAdminController.class)
public class TagAdminControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagServiceImpl tagService;

    @Test
    public void tag_test19_DeleteTagTest() throws Exception {
        doNothing().when(tagService).deleteTag(anyLong(), any());
        mockMvc.perform(delete("/api/v1/admin/tags/1")
                        .header("X-Current-User-Id", 1))
                .andExpect(status().isOk());
    }
}
