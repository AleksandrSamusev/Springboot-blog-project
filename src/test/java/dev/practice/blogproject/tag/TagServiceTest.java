package dev.practice.blogproject.tag;
import dev.practice.blogproject.dtos.tag.TagFullDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.repositories.*;
import dev.practice.blogproject.services.impl.TagServiceImpl;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.models.*;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceTest {
    @Mock
    UserRepository userRepositoryMock;
    @Mock
    ArticleRepository articleRepositoryMock;
    @Mock
    TagRepository tagRepositoryMock;
    @InjectMocks
    TagServiceImpl tagService;

    private final User author = new User(1L, "Harry", "Potter",
            "harryPotter", "harrypotter@test.test",
            LocalDate.of(2000, 12, 27), Role.USER, "Hi! I'm Harry", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final Article article = new Article(1L, "Potions",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 1450L, new HashSet<>(), new HashSet<>());

    private final User admin = new User(10L, "Kirk", "Douglas",
            "kirkDouglas", "kirkdouglas@test.test",
            LocalDate.of(1955, 3, 9), Role.ADMIN, "Hi! I'm Admin", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final User notAdmin = new User(5L, "Alex", "Ferguson",
            "alexFerguson", "alexferguson@test.test",
            LocalDate.of(1980, 6, 16), Role.USER, "Hi! I'm Alex", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());


    @Test
    public void tag_test1_When_GetAllArticleTags_Then_ReturnListOfTags() {
        when(articleRepositoryMock.findById(anyLong())).thenReturn(Optional.of(article));
        when(articleRepositoryMock.existsById(anyLong())).thenReturn(Boolean.TRUE);
        Set<Article> articles = new HashSet<>();
        articles.add(article);
        Tag tag = new Tag(1L, "tag1", articles);
        article.getTags().add(tag);
        List<TagFullDto> result = tagService.getAllArticleTags(article.getArticleId());
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getTagId(), tag.getTagId());
        assertEquals(result.get(0).getName(), tag.getName());
    }

    @Test
    public void tag_test2_Given_ArticleNotExists_When_GetAllArticleTags_Then_ResourceNotFound() {
        when(articleRepositoryMock.existsById(anyLong())).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                tagService.getAllArticleTags(5L));
        assertEquals("Article with given ID = 5 not found", ex.getMessage());
    }

    @Test
    public void tag_test3_Given_ArticleIdIsNull_When_GetAllArticleTags_Then_InvalidParameter() {
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                tagService.getAllArticleTags(null));
        assertEquals("Id parameter cannot be null", ex.getMessage());
    }

    @Test
    public void tag_test4_Given_ValidId_When_GetTagById_Then_TagReturns() {
        Set<Article> articles = new HashSet<>();
        articles.add(article);
        Tag tag = new Tag(1L, "tag1", articles);
        article.getTags().add(tag);
        when(tagRepositoryMock.existsById(tag.getTagId())).thenReturn(true);
        when(tagRepositoryMock.findById(anyLong())).thenReturn(Optional.of(tag));
        TagFullDto result = tagService.getTagById(tag.getTagId());
        assertEquals(result.getTagId(), tag.getTagId());
        assertEquals(result.getName(), tag.getName());
        assertEquals(result.getArticles().size(), 1);
    }

    @Test
    public void tag_test5_Given_TagNotExists_When_GetTagById_Then_ResourceNotFound() {
        Set<Article> articles = new HashSet<>();
        articles.add(article);
        Tag tag = new Tag(1L, "tag1", articles);
        article.getTags().add(tag);
        when(tagRepositoryMock.existsById(anyLong())).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, ()->
                tagService.getTagById(tag.getTagId()));
        assertEquals("Tag with given ID = 1 not found", ex.getMessage());
    }

    @Test
    public void tag_test6_Given_IdIsNull_When_GetTagById_Then_InvalidParameterException() {
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, ()->
                tagService.getTagById(null));
        assertEquals("Id parameter cannot be null", ex.getMessage());
    }

    @Test
    public void tag_test7_Given_ValidParameters_When_createTag_Then_TagCreated() {
        TagNewDto newTag = new TagNewDto("tag1");
        Set<Article> articles = new HashSet<>();
        articles.add(article);
        Tag tag = new Tag(1L, "tag1", articles);
        article.getTags().add(tag);
        when(articleRepositoryMock.findById(anyLong())).thenReturn(Optional.of(article));
        when(tagRepositoryMock.save(any())).thenReturn(tag);
        when(articleRepositoryMock.save(any())).thenReturn(article);

        TagFullDto result = tagService.createTag(newTag, article.getArticleId());

        assertEquals(result.getTagId(), tag.getTagId());
        assertEquals(result.getName(), tag.getName());
        assertEquals(result.getArticles().size(), 1);
    }

    @Test
    public void tag_test8_Given_ArticleNotExists_When_createTag_Then_ResourceNotFound() {
        TagNewDto newTag = new TagNewDto("tag1");
        when(articleRepositoryMock.existsById(anyLong())).thenReturn(false);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, ()->
                tagService.createTag(newTag, article.getArticleId()));
        assertEquals("Article with given ID = 1 not found", ex.getMessage());
    }

    @Test
    public void tag_test9_Given_TagNameAlreadyExists_When_createTag_Then_InvalidParameter() {
        TagNewDto newTag = new TagNewDto("tag1");
        Set<Article> articles = new HashSet<>();
        articles.add(article);
        Tag tag = new Tag(1L, "tag1", articles);

        when(articleRepositoryMock.existsById(anyLong())).thenReturn(true);
        when(tagRepositoryMock.findTagByName(any())).thenReturn(tag);
        when(articleRepositoryMock.findById(anyLong())).thenReturn(Optional.of(article));

        InvalidParameterException ex = assertThrows(InvalidParameterException.class, ()->
                tagService.createTag(newTag, article.getArticleId()));
        assertEquals("Tag with given name = tag1 already exists", ex.getMessage());
    }

    @Test
    public void tag_test10_Given_ArticleIdIsNull_When_CreateTag_The_InvalidParameter() {
        TagNewDto newTag = new TagNewDto("tag1");
        InvalidParameterException ex = assertThrows(InvalidParameterException.class, ()->
                tagService.createTag(newTag, null));
        assertEquals("Id parameter cannot be null", ex.getMessage());
    }

    @Test
    public void tag_test11_Given_ValidIds_When_DeleteTag_TagDeleted() {
        when(userRepositoryMock.existsById(anyLong())).thenReturn(true);
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(admin));
        when(tagRepositoryMock.existsById(anyLong())).thenReturn(true);
        doNothing().when(tagRepositoryMock).deleteById(1L);
        tagService.deleteTag(1L, admin.getUserId());
        verify(tagRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    public void tag_test12_Given_UserIsNotAdmin_When_DeleteTag_Then_ActionForbidden() {
        when(userRepositoryMock.existsById(anyLong())).thenReturn(true);
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(notAdmin));
        doNothing().when(tagRepositoryMock).deleteById(1L);

        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, ()->
        tagService.deleteTag(1L, admin.getUserId()));
        assertEquals("Action forbidden for current user", ex.getMessage());
    }

    @Test
    public void tag_test13_Given_TagNotExists_When_DeleteTag_Then_ResourceNotFound() {
        when(userRepositoryMock.existsById(anyLong())).thenReturn(true);
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(admin));
        when(tagRepositoryMock.existsById(anyLong())).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, ()->
                tagService.deleteTag(1L, admin.getUserId()));
        assertEquals("Tag with given ID = 1 not found", ex.getMessage());
    }

    @Test
    public void tag_test14_Given_UserNotExists_When_DeleteTag_Then_ResourceNotFound() {
        when(userRepositoryMock.existsById(anyLong())).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, ()->
                tagService.deleteTag(1L, admin.getUserId()));
        assertEquals("User with given ID = 10 not found", ex.getMessage());
    }

    @Test
    public void tag_test15_Given_TagIdIsNull_When_DeleteTag_Then_InvalidParameter() {

        InvalidParameterException ex = assertThrows(InvalidParameterException.class, ()->
                tagService.deleteTag(null, admin.getUserId()));
        assertEquals("Id parameter cannot be null", ex.getMessage());
    }
}
