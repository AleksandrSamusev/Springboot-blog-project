package dev.practice.blogproject.article;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.dtos.tag.TagShortDto;
import dev.practice.blogproject.models.*;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.TagRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.ArticlePrivateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    private final Article article = new Article(null, "The empty pot",
            "Very interesting information", user, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, new HashSet<>(), new HashSet<>());
    private final ArticleNewDto newArticle = new ArticleNewDto("The empty pot",
            "Very interesting information", new HashSet<>());
    private final Article savedArticle = new Article(0L, "The empty pot",
            "Very interesting information", user, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, new HashSet<>(), new HashSet<>());
    private final Tag tag1 = new Tag(null, "Potions", new HashSet<>());

    @Test
    void article_test_1_Given_validArticleWithNewTags_When_createArticle_Then_articleSaved() {
        newArticle.getTags().add(new TagNewDto(tag1.getName()));
        User author = userRepository.save(user);

        ArticleFullDto result = articleService.createArticle(author.getUserId(), newArticle);
        List<TagShortDto> tags = result.getTags().stream().toList();

        Tag tag  = tagRepository.findTagByName(tag1.getName());
        assertThat(result).isNotNull();
        assertThat(tags).isEqualTo(1);
        assertThat(tags.get(0).getName()).isEqualTo(tag1.getName());
        assertThat(tagRepository.getReferenceById(0L).getArticles().size()).isEqualTo(1);
        Mockito.verify(articleRepository, Mockito.times(1)).save(Mockito.any(Article.class));
    }
}
