package dev.practice.mainApp.tag;

import dev.practice.mainApp.dtos.tag.TagFullDto;
import dev.practice.mainApp.dtos.tag.TagNewDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.TagRepository;
import dev.practice.mainApp.services.impl.TagServiceImpl;
import dev.practice.mainApp.utils.Validations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceTest {
    @Mock
    private ArticleRepository articleRepositoryMock;
    @Mock
    private TagRepository tagRepositoryMock;
    @Mock
    private Validations validations;
    @InjectMocks
    private TagServiceImpl tagService;

    /*private final User author = new User(1L, "Harry", "Potter",
            "harryPotter", "harrypotter@test.test",
            LocalDate.of(2000, 12, 27), Role.USER, "Hi! I'm Harry", false,
            new HashSet<Message>(), new HashSet<Message>(), new HashSet<Article>(), new HashSet<Comment>());

    private final Article article = new Article(1L, "Potions",
            "Very interesting information", author, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 1450L, 0L, new HashSet<>(), new HashSet<>());

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
        when(validations.checkArticleExist(anyLong())).thenReturn(article);
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
        doThrow(new ResourceNotFoundException(
                "Article with id 5 wasn't found")).when(validations).checkArticleExist(anyLong());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                tagService.getAllArticleTags(5L));
        assertEquals("Article with id 5 wasn't found", ex.getMessage());
    }

    @Test
    public void tag_test4_Given_ValidId_When_GetTagById_Then_TagReturns() {
        Set<Article> articles = new HashSet<>();
        articles.add(article);
        Tag tag = new Tag(1L, "tag1", articles);
        article.getTags().add(tag);
        when(validations.isTagExists(anyLong())).thenReturn(tag);

        TagFullDto result = tagService.getTagById(tag.getTagId());

        assertEquals(result.getTagId(), tag.getTagId());
        assertEquals(result.getName(), tag.getName());
        assertEquals(result.getArticles().size(), 1);
    }

    @Test
    public void tag_test5_Given_TagNotExists_When_GetTagById_Then_ResourceNotFound() {
        doThrow(new ResourceNotFoundException(
                "Tag with given ID = 1 not found")).when(validations).isTagExists(anyLong());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                tagService.getTagById(1L));
        assertEquals("Tag with given ID = 1 not found", ex.getMessage());
    }

    @Test
    public void tag_test7_Given_ValidParameters_When_createTag_Then_TagCreated() {
        TagNewDto newTag = new TagNewDto("tag1");
        Set<Article> articles = new HashSet<>();
        articles.add(article);
        Tag tag = new Tag(1L, "tag1", articles);
        article.getTags().add(tag);
        when(validations.checkArticleExist(anyLong())).thenReturn(article);
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
        doThrow(new ResourceNotFoundException(
                "Article with id 1 wasn't found")).when(validations).checkArticleExist(anyLong());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                tagService.createTag(newTag, article.getArticleId()));
        assertEquals("Article with id 1 wasn't found", ex.getMessage());
    }

    @Test
    public void tag_test9_Given_TagNameAlreadyExists_When_createTag_Then_InvalidParameter() {
        TagNewDto newTag = new TagNewDto("tag1");
        Set<Article> articles = new HashSet<>();
        articles.add(article);
        Tag tag = new Tag(1L, "tag1", articles);

        when(validations.checkArticleExist(anyLong())).thenReturn(article);
        when(tagRepositoryMock.findTagByName(any())).thenReturn(tag);

        InvalidParameterException ex = assertThrows(InvalidParameterException.class, () ->
                tagService.createTag(newTag, article.getArticleId()));
        assertEquals("Tag with given name = tag1 already exists", ex.getMessage());
    }

    @Test
    public void tag_test11_Given_ValidIds_When_DeleteTag_TagDeleted() {
        Tag tag = new Tag(1L, "tag1", Set.of(article));
        when(validations.checkUserExist(anyLong())).thenReturn(admin);
        when(validations.isTagExists(anyLong())).thenReturn(tag);
        doNothing().when(tagRepositoryMock).deleteById(1L);

        tagService.deleteTag(1L, admin.getUserId());

        verify(tagRepositoryMock, times(1)).deleteById(1L);
    }

    @Test
    public void tag_test12_Given_UserIsNotAdmin_When_DeleteTag_Then_ActionForbidden() {
        doThrow(new ActionForbiddenException(
                "User with id 5 is not ADMIN. Access is forbidden")).when(validations).checkUserIsAdmin(any());

        ActionForbiddenException ex = assertThrows(ActionForbiddenException.class, () ->
                tagService.deleteTag(1L, notAdmin.getUserId()));
        assertEquals("User with id 5 is not ADMIN. Access is forbidden", ex.getMessage());
    }

    @Test
    public void tag_test13_Given_TagNotExists_When_DeleteTag_Then_ResourceNotFound() {
        doThrow(new ResourceNotFoundException(
                "Tag with given ID = 1 not found")).when(validations).isTagExists(anyLong());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                tagService.deleteTag(1L, admin.getUserId()));
        assertEquals("Tag with given ID = 1 not found", ex.getMessage());
    }

    @Test
    public void tag_test14_Given_UserNotExists_When_DeleteTag_Then_ResourceNotFound() {
        doThrow(new ResourceNotFoundException(
                "User with id 1 wasn't found")).when(validations).checkUserExist(1L);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                tagService.deleteTag(1L, 1L));
        assertEquals("User with id 1 wasn't found", ex.getMessage());
    }*/
}