package dev.practice.blogproject.tag;

import dev.practice.blogproject.controllers._private.PrivateCommentController;
import dev.practice.blogproject.controllers._public.PublicTagController;
import dev.practice.blogproject.dtos.comment.CommentFullDto;
import dev.practice.blogproject.dtos.comment.CommentNewDto;
import dev.practice.blogproject.dtos.tag.TagFullDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.dtos.user.UserShortDto;
import dev.practice.blogproject.models.*;
import dev.practice.blogproject.services.impl.CommentServiceImpl;
import dev.practice.blogproject.services.impl.TagServiceImpl;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicTagController.class)
public class PublicTagControllerTest {

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
    public void tag_test18_GetTagByIdTest() throws Exception{
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
