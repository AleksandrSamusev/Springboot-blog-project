package dev.practice.mainApp.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.mainApp.controllers._private.TagPrivateController;
import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.tag.TagFullDto;
import dev.practice.mainApp.dtos.tag.TagNewDto;
import dev.practice.mainApp.dtos.tag.TagShortDto;
import dev.practice.mainApp.dtos.user.UserShortDto;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.repositories.TagRepository;
import dev.practice.mainApp.services.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagPrivateController.class)
public class TagPrivateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagServiceImpl tagService;

    @MockBean
    private TagRepository tagRepository;

    @Autowired
    private ObjectMapper mapper;

    TagNewDto newTag = new TagNewDto("New Tag");

    TagFullDto fullDto = new TagFullDto(1L, "New Tag", Set.of(1L));

    private final ArticleFullDto articleFull = new ArticleFullDto(1L, "The empty pot",
            "Very interesting information", new UserShortDto(1L, "Harry"),
            LocalDateTime.now(), null, ArticleStatus.CREATED, 0L, 0L, new HashSet<>(), new HashSet<>());

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

    @Test
    void tag_test_26_Given_newTagsNotExistInDB_When_addTagsToArticle_Then_tagsAddedStatusOk() throws Exception {
        articleFull.getTags().add(new TagShortDto(1L, "new tag"));
        when(tagService.addTagsToArticle(any(), anyLong(), any())).thenReturn(articleFull);

        mockMvc.perform(patch("/api/v1/private/tags/articles/{articleId}/add", 1L)
                        .header("X-Current-User-Id", 1)
                        .param("tags", String.valueOf(List.of(String.valueOf(newTag))))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1))
                .andExpect(jsonPath("$.title").value(articleFull.getTitle()))
                .andExpect(jsonPath("$.content").value(articleFull.getContent()))
                .andExpect(jsonPath("$.author.userId").value(articleFull.getAuthor().getUserId().intValue()))
                .andExpect(jsonPath("$.author.username").value(articleFull.getAuthor().getUsername()))
                .andExpect(jsonPath("$.created").value(notNullValue()))
                .andExpect(jsonPath("$.published").value(nullValue()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.tags[0].name").value(newTag.getName().toLowerCase()));

        Mockito.verify(tagService, Mockito.times(1))
                .addTagsToArticle(Mockito.any(), Mockito.anyLong(), Mockito.any());
    }

    @Test
    void tag_test_29_Given_listTags_When_removeTagsFromArticle_Then_tegRemoved() throws Exception {
        when(tagService.removeTagsFromArticle(any(), anyLong(), any())).thenReturn(articleFull);

        mockMvc.perform(patch("/api/v1/private/tags/articles/{articleId}/remove", 1L)
                        .header("X-Current-User-Id", 1)
                        .param("tags", String.valueOf(1L), String.valueOf(2L))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articleId").value(1))
                .andExpect(jsonPath("$.title").value(articleFull.getTitle()))
                .andExpect(jsonPath("$.content").value(articleFull.getContent()))
                .andExpect(jsonPath("$.author.userId").value(articleFull.getAuthor().getUserId().intValue()))
                .andExpect(jsonPath("$.author.username").value(articleFull.getAuthor().getUsername()))
                .andExpect(jsonPath("$.created").value(notNullValue()))
                .andExpect(jsonPath("$.published").value(nullValue()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.likes").value(0))
                .andExpect(jsonPath("$.comments").isEmpty())
                .andExpect(jsonPath("$.tags").isEmpty());

        Mockito.verify(tagService, Mockito.times(1))
                .removeTagsFromArticle(Mockito.any(), Mockito.anyLong(), Mockito.any());
    }
}
