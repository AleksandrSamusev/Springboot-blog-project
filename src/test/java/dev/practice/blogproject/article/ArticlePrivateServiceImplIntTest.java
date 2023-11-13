package dev.practice.blogproject.article;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleUpdateDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.dtos.tag.TagShortDto;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.models.*;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.TagRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.ArticlePrivateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

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
    private final Article article = new Article(null, "The empty pot",
            "Very interesting information", user, LocalDateTime.now(), LocalDateTime.now(),
            ArticleStatus.PUBLISHED, 1450L, new HashSet<>(), new HashSet<>());
    private final Article article2 = new Article(null, "A pretty cat",
            "Very interesting information", user, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, new HashSet<>(), new HashSet<>());


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

    @Test
    void article_test_9_Given_validNewTitle_When_updateArticle_Then_articleUpdatedCorrectly() {
        article.getTags().add(tag1);
        article.getTags().add(tag2);
        User author = userRepository.save(user);
        Article articleSaved = articleRepository.save(article); // likes = 1450, status = PUBLISHED, published != null
        tag1.getArticles().add(articleSaved);
        tag2.getArticles().add(articleSaved);
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        ArticleUpdateDto update = new ArticleUpdateDto();
        update.setTitle("new title");

        ArticleFullDto result = articleService.updateArticle(author.getUserId(), article.getArticleId(), update);

        assertThat(result).isNotNull();
        assertThat(result.getArticleId()).isEqualTo(articleSaved.getArticleId());
        assertThat(result.getTitle()).isEqualTo(update.getTitle());
        assertThat(result.getContent()).isEqualTo(articleSaved.getContent());
        assertThat(result.getAuthor().getUserId()).isEqualTo(articleSaved.getAuthor().getUserId());
        assertThat(result.getAuthor().getUsername()).isEqualTo(articleSaved.getAuthor().getUsername());
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.CREATED);
        assertThat(result.getCreated()).isEqualTo(articleSaved.getCreated());
        assertThat(result.getPublished()).isNull();
        assertThat(result.getLikes()).isEqualTo(0);
        assertThat(result.getComments().size()).isEqualTo(0);
        assertThat(result.getTags().size()).isEqualTo(2);
    }

    @Test
    void article_test_10_Given_validNewContent_When_updateArticle_Then_articleUpdatedCorrectly() {
        article.getTags().add(tag1);
        article.getTags().add(tag2);
        User author = userRepository.save(user);
        Article articleSaved = articleRepository.save(article); // likes = 1450, status = PUBLISHED, published != null
        tag1.getArticles().add(articleSaved);
        tag2.getArticles().add(articleSaved);
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        ArticleUpdateDto update = new ArticleUpdateDto();
        update.setContent("new content");

        ArticleFullDto result = articleService.updateArticle(author.getUserId(), article.getArticleId(), update);

        assertThat(result).isNotNull();
        assertThat(result.getArticleId()).isEqualTo(articleSaved.getArticleId());
        assertThat(result.getTitle()).isEqualTo(articleSaved.getTitle());
        assertThat(result.getContent()).isEqualTo(update.getContent());
        assertThat(result.getAuthor().getUserId()).isEqualTo(articleSaved.getAuthor().getUserId());
        assertThat(result.getAuthor().getUsername()).isEqualTo(articleSaved.getAuthor().getUsername());
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.CREATED);
        assertThat(result.getCreated()).isEqualTo(articleSaved.getCreated());
        assertThat(result.getPublished()).isNull();
        assertThat(result.getLikes()).isEqualTo(0);
        assertThat(result.getComments().size()).isEqualTo(0);
        assertThat(result.getTags().size()).isEqualTo(2);
    }

    @Test
    void article_test_11_Given_validNewContentAndTitle_When_updateArticle_Then_articleUpdatedCorrectly() {
        article.getTags().add(tag1);
        article.getTags().add(tag2);
        User author = userRepository.save(user);
        Article articleSaved = articleRepository.save(article); // likes = 1450, status = PUBLISHED, published != null
        tag1.getArticles().add(articleSaved);
        tag2.getArticles().add(articleSaved);
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        ArticleUpdateDto update = new ArticleUpdateDto();
        update.setContent("new content");
        update.setTitle("newTitle");

        ArticleFullDto result = articleService.updateArticle(author.getUserId(), article.getArticleId(), update);

        assertThat(result).isNotNull();
        assertThat(result.getArticleId()).isEqualTo(articleSaved.getArticleId());
        assertThat(result.getTitle()).isEqualTo(update.getTitle());
        assertThat(result.getContent()).isEqualTo(update.getContent());
        assertThat(result.getAuthor().getUserId()).isEqualTo(articleSaved.getAuthor().getUserId());
        assertThat(result.getAuthor().getUsername()).isEqualTo(articleSaved.getAuthor().getUsername());
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.CREATED);
        assertThat(result.getCreated()).isEqualTo(articleSaved.getCreated());
        assertThat(result.getPublished()).isNull();
        assertThat(result.getLikes()).isEqualTo(0);
        assertThat(result.getComments().size()).isEqualTo(0);
        assertThat(result.getTags().size()).isEqualTo(2);
    }

    @Test
    void article_test_14_Given_titleAlreadyExist_When_updateArticle_Then_throwException() {
        User author = userRepository.save(user);
        articleRepository.save(article);
        Article articleSaved2 = articleRepository.save(article2);
        ArticleUpdateDto update = new ArticleUpdateDto();
        update.setTitle("THE EMPTY POT   ");

        final InvalidParameterException exception = Assertions.assertThrows(InvalidParameterException.class,
                () -> articleService.updateArticle(author.getUserId(), articleSaved2.getArticleId(), update));
        assertEquals("Article with title THE EMPTY POT    already exist",
                exception.getMessage(), "Incorrect message");
        assertThrows(InvalidParameterException.class, () -> articleService.updateArticle(
                author.getUserId(), articleSaved2.getArticleId(), update), "Incorrect exception");
    }


}
