package dev.practice.blogproject.article;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.dtos.tag.TagShortDto;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.Role;
import dev.practice.blogproject.models.Tag;
import dev.practice.blogproject.models.User;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.TagRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.ArticlePrivateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ArticlePrivateServiceImplIntTest {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ArticlePrivateService articleService;

    private final User user = new User(null, "Harry", "Potter", "HP",
            "hp@gmail.com", LocalDate.of(1981, 7, 31), Role.USER, null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final Tag tag1 = new Tag(null, "Potions", new HashSet<>());
    private final Tag tag2 = new Tag(null, "Cat", new HashSet<>());
    private final ArticleNewDto newArticle = new ArticleNewDto("The empty pot",
            "Very interesting information", new HashSet<>());
    private final ArticleNewDto newArticle2 = new ArticleNewDto("Pot", "Interesting information",
            new HashSet<>());


    @Test
    void article_test_2_Given_validArticleWithNewTags_When_createArticle_Then_articleSavedWithTag() {
        newArticle.getTags().add(new TagNewDto(tag1.getName()));
        User author = userRepository.save(user);

        ArticleFullDto result = articleService.createArticle(author.getUserId(), newArticle);

        List<TagShortDto> tags = result.getTags().stream().toList();
        Tag tag = tagRepository.findTagByName(tag1.getName());

        assertThat(result).isNotNull();
        assertThat(tags.size()).isEqualTo(1);
        assertThat(tags.get(0).getName()).isEqualTo(tag1.getName());
        assertThat(tagRepository.getReferenceById(tag.getTagId()).getArticles().size()).isEqualTo(1);
    }

    @Test
    void article_test_3_Given_validArticleWithExistTag_When_createArticle_Then_articleSavedWithTag() {
        newArticle.getTags().add(new TagNewDto(tag1.getName()));
        newArticle2.getTags().add(new TagNewDto(tag1.getName()));
        User author = userRepository.save(user);
        articleService.createArticle(author.getUserId(), newArticle);

        ArticleFullDto result = articleService.createArticle(author.getUserId(), newArticle2);

        List<TagShortDto> tags = result.getTags().stream().toList();
        Tag tag = tagRepository.findTagByName(tag1.getName());

        assertThat(result).isNotNull();
        assertThat(tags.size()).isEqualTo(1);
        assertThat(tags.get(0).getName()).isEqualTo(tag1.getName());
        assertThat(tagRepository.getReferenceById(tag.getTagId()).getArticles().size()).isEqualTo(2);
    }

    @Test
    void article_test_4_Given_validArticleWithExistAndNewTags_When_createArticle_Then_articleSavedWithTags() {
        newArticle.getTags().add(new TagNewDto(tag1.getName()));
        newArticle2.getTags().add(new TagNewDto(tag1.getName()));
        newArticle2.getTags().add(new TagNewDto(tag2.getName()));
        User author = userRepository.save(user);
        articleService.createArticle(author.getUserId(), newArticle);

        ArticleFullDto result = articleService.createArticle(author.getUserId(), newArticle2);

        List<TagShortDto> tags = result.getTags().stream().sorted(Comparator.comparing(TagShortDto::getName)).toList();
        Tag tag = tagRepository.findTagByName(tag1.getName());
        Tag tag3 = tagRepository.findTagByName(tag2.getName());

        assertThat(result).isNotNull();
        assertThat(tags.size()).isEqualTo(2);
        assertThat(tags.get(1).getName()).isEqualTo(tag1.getName());
        assertThat(tags.get(0).getName()).isEqualTo(tag2.getName());
        assertThat(tagRepository.getReferenceById(tag.getTagId()).getArticles().size()).isEqualTo(2);
        assertThat(tagRepository.getReferenceById(tag3.getTagId()).getArticles().size()).isEqualTo(1);
    }

    @Test
    void article_test_5_Given_articleWithTitleAlreadyExist_When_createArticle_Then_throwException() {
        User author = userRepository.save(user);
        articleService.createArticle(author.getUserId(), newArticle);


        final InvalidParameterException exception = Assertions.assertThrows(InvalidParameterException.class,
                () -> articleService.createArticle(author.getUserId(), newArticle));
        assertEquals("Article with title The empty pot already exist", exception.getMessage(),
                "Incorrect message");
        assertThrows(InvalidParameterException.class, () -> articleService.createArticle(
                author.getUserId(), newArticle), "Incorrect exception");
    }



}
