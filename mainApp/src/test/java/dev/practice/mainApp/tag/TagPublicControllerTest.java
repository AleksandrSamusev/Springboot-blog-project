package dev.practice.mainApp.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.controllers._public.TagPublicController;
import dev.practice.mainApp.dtos.tag.TagFullDto;
import dev.practice.mainApp.dtos.tag.TagNewDto;
import dev.practice.mainApp.services.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagPublicController.class)
public class TagPublicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagServiceImpl tagService;

    @Autowired
    private ObjectMapper mapper;

    TagNewDto newTag = new TagNewDto("New Tag");

    TagFullDto fullDto = new TagFullDto(1L, "New Tag", Set.of(1L));

    @Test
    public void tag_test17_GetAllArticleTagsTest() throws Exception {

        when(tagService.getAllArticleTags(anyLong())).thenReturn(List.of(fullDto));
        mockMvc.perform(get("/api/v1/public/tags/articles/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].tagId").value(fullDto.getTagId()))
                .andExpect(jsonPath("$.[0].name").value(fullDto.getName()))
                .andExpect(jsonPath("$.[0].articles.size()").value(1));
    }

    @Test
    public void tag_test18_GetTagByIdTest() throws Exception {
        when(tagService.getTagById(anyLong())).thenReturn(fullDto);

        mockMvc.perform(get("/api/v1/public/tags/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagId").value(fullDto.getTagId()))
                .andExpect(jsonPath("$.name").value(fullDto.getName()))
                .andExpect(jsonPath("$.articles.size()").value(1));
    }
}
