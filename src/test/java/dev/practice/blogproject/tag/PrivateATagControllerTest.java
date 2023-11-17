package dev.practice.blogproject.tag;

import dev.practice.blogproject.controllers._private.PrivateTagController;
import dev.practice.blogproject.dtos.tag.TagFullDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.services.impl.TagServiceImpl;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateTagController.class)
public class PrivateATagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagServiceImpl tagService;

    @Autowired
    private ObjectMapper mapper;

    TagNewDto newTag = new TagNewDto("New Tag");

    TagFullDto fullDto = new TagFullDto(1L, "New Tag", Set.of(1L));

    @Test
    public void tag_test16_CreateTagTest() throws Exception {

        when(tagService.createTag(any(), anyLong())).thenReturn(fullDto);

        mockMvc.perform(post("/api/v1/private/tags/articles/1")
                        .header("X-Current-User-Id", 1)
                        .content(mapper.writeValueAsString(newTag))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tagId").value(1))
                .andExpect(jsonPath("$.name").value(newTag.getName()))
                .andExpect(jsonPath("$.articles.size()").value(1));
    }

    @Test
    public void tag_test20_Given_TagNameIsNull_When_CreateTagTest_Then_BadRequest() throws Exception {

        TagNewDto newTag = new TagNewDto(null);

        when(tagService.createTag(any(), anyLong())).thenReturn(fullDto);

        mockMvc.perform(post("/api/v1/private/tags/articles/1")
                        .header("X-Current-User-Id", 1)
                        .content(mapper.writeValueAsString(newTag))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Tag name cannot be blank")));
    }

    @Test
    public void tag_test21_Given_TagNameLengthIs60Chars_When_CreateTagTest_Then_BadRequest() throws Exception {

        TagNewDto newTag = new TagNewDto("012345678901234567890123456789012345678901234567890123456789");

        when(tagService.createTag(any(), anyLong())).thenReturn(fullDto);

        mockMvc.perform(post("/api/v1/private/tags/articles/1")
                        .header("X-Current-User-Id", 1)
                        .content(mapper.writeValueAsString(newTag))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]", is("Name length should be 50 chars max")));
    }
}
