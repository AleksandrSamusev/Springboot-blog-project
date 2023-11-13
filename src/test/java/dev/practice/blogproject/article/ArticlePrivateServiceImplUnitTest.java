package dev.practice.blogproject.article;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.dtos.tag.TagShortDto;
import dev.practice.blogproject.models.*;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.TagRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.impl.ArticlePrivateServiceImpl;
import dev.practice.blogproject.services.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class ArticlePrivateServiceImplUnitTest {
    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private ArticlePrivateServiceImpl articleService;

    @InjectMocks
    private TagServiceImpl tagService;

    private final User author = new User(0L, "Harry", "Potter", "HP",
            "hp@gmail.com", LocalDate.of(1981, 7, 31), Role.USER, null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final Article article = new Article(null, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, new HashSet<>(), new HashSet<>());
    private final ArticleNewDto newArticle = new ArticleNewDto("The empty pot",
            "Very interesting information", new HashSet<>());
    private final Article savedArticle = new Article(0L, "The empty pot",
            "Very interesting information", author, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, new HashSet<>(), new HashSet<>());
    private final Tag tag1 = new Tag(0L, "Potions", new HashSet<>());


    @Test
    void article_test_1_Given_validArticleWithoutTagsAndUser_When_createArticle_Then_articleSaved() {
        Mockito
                .when(userRepository.findById(0L))
                .thenReturn(Optional.of(author));
        Mockito
                .when(articleRepository.save(Mockito.any(Article.class)))
                .thenReturn(savedArticle);
        Mockito
                .when(articleRepository.getReferenceById(0L))
                .thenReturn(savedArticle);

        ArticleFullDto result = articleService.createArticle(0L, newArticle);

        assertThat(result).isNotNull();
        assertThat(result.getTags()).isEqualTo(new HashSet<>());
        assertThat(result.getTitle()).isEqualTo(savedArticle.getTitle());
        assertThat(result.getContent()).isEqualTo(savedArticle.getContent());
        assertThat(result.getAuthor().getUserId()).isEqualTo(0L);
        assertThat(result.getAuthor().getUsername()).isEqualTo("HP");
        assertThat(result.getStatus()).isEqualTo(ArticleStatus.CREATED);
        Mockito.verify(articleRepository, Mockito.times(1)).save(Mockito.any(Article.class));
    }

    @Test
    void article_test_2_Given_validArticleWithNewTags_When_createArticle_Then_articleSaved() {
        newArticle.setTags(Set.of(new TagNewDto(tag1.getName())));
        tag1.getArticles().add(article);

        Mockito
                .when(userRepository.findById(0L))
                .thenReturn(Optional.of(author));
        Mockito
                .when(articleRepository.save(Mockito.any(Article.class)))
                .thenReturn(savedArticle);
        Mockito
                .when(articleRepository.getReferenceById(0L))
                .thenReturn(savedArticle);
        Mockito
                .when(tagRepository.save(Mockito.any(Tag.class)))
                .thenReturn(tag1);
        Mockito
                .when(tagRepository.findTagByName(tag1.getName()))
                .thenReturn(tag1);

        ArticleFullDto result = articleService.createArticle(0L, newArticle);
        List<TagShortDto> tags = result.getTags().stream().toList();

        assertThat(result).isNotNull();
        assertThat(tags).isEqualTo(1);
        assertThat(tags.get(0).getName()).isEqualTo(tag1.getName());
        assertThat(tagRepository.getReferenceById(0L).getArticles().size()).isEqualTo(1);
        Mockito.verify(articleRepository, Mockito.times(1)).save(Mockito.any(Article.class));
    }
}
