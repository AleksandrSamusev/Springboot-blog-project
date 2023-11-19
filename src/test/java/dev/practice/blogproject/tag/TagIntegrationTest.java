package dev.practice.blogproject.tag;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.dtos.tag.TagShortDto;
import dev.practice.blogproject.models.Role;
import dev.practice.blogproject.models.User;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.TagRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.ArticlePrivateService;
import dev.practice.blogproject.services.TagService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TagIntegrationTest {
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final ArticlePrivateService articleService;

    private final User author = new User(null, "Harry", "Potter", "HP",
            "hp@gmail.com", LocalDate.of(1981, 7, 31), Role.USER, null,
            false, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
    private final TagNewDto newTag1 = new TagNewDto("tag1");
    private final TagNewDto newTag2 = new TagNewDto("tag2");
    private final ArticleNewDto newArticle = new ArticleNewDto("The empty pot",
            "Very interesting information", new HashSet<>());
    private final ArticleNewDto newArticle2 = new ArticleNewDto("Pot", "Interesting information",
            new HashSet<>());

    @Test
    void tag_test_22_Given_newTagsNotExistInDBArticleNotContainIt_When_addTagsToArticle_Then_tagsAdded() {
        dropDB();
        User savedAuthor = userRepository.save(author);
        newArticle.getTags().add(newTag1);
        ArticleFullDto savedArticle = articleService.createArticle(savedAuthor.getUserId(), newArticle);

        ArticleFullDto result = tagService.addTagsToArticle(
                savedAuthor.getUserId(), savedArticle.getArticleId(), List.of(newTag2));
        List<TagShortDto> tags = result.getTags().stream().sorted(Comparator.comparing(TagShortDto::getTagId)).toList();

        assertThat(result).isNotNull();
        assertThat(result.getTags().size()).isEqualTo(2);
        assertThat(tags.get(0).getName()).isEqualTo(newTag1.getName());
        assertThat(tags.get(1).getName()).isEqualTo(newTag2.getName());
    }

    @Test
    void tag_test_23_Given_oldTagsExistInDBArticleNotContainIt_When_addTagsToArticle_Then_tagsAdded() {
        dropDB();
        User savedAuthor = userRepository.save(author);
        newArticle.getTags().add(newTag1);
        newArticle2.getTags().add(newTag2);
        ArticleFullDto savedArticle = articleService.createArticle(savedAuthor.getUserId(), newArticle);
        articleService.createArticle(savedAuthor.getUserId(), newArticle2);

        ArticleFullDto result = tagService.addTagsToArticle(
                savedAuthor.getUserId(), savedArticle.getArticleId(), List.of(newTag2));
        List<TagShortDto> tags = result.getTags().stream().sorted(Comparator.comparing(TagShortDto::getTagId)).toList();

        assertThat(result).isNotNull();
        assertThat(result.getTags().size()).isEqualTo(2);
        assertThat(tags.get(0).getName()).isEqualTo(newTag1.getName());
        assertThat(tags.get(1).getName()).isEqualTo(newTag2.getName());
        assertThat(tagRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    void tag_test_24_Given_oldTagsExistInDBArticleContainIt_When_addTagsToArticle_Then_tagsNotChanged() {
        dropDB();
        User savedAuthor = userRepository.save(author);
        newArticle.getTags().add(newTag1);
        ArticleFullDto savedArticle = articleService.createArticle(savedAuthor.getUserId(), newArticle);

        ArticleFullDto result = tagService.addTagsToArticle(
                savedAuthor.getUserId(), savedArticle.getArticleId(), List.of(newTag1));
        List<TagShortDto> tags = result.getTags().stream().toList();

        assertThat(result).isNotNull();
        assertThat(result.getTags().size()).isEqualTo(1);
        assertThat(tags.get(0).getName()).isEqualTo(newTag1.getName());
        assertThat(tagRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void tag_test_25_Given_emptyTags_When_addTagsToArticle_Then_tagsNotChanged() {
        dropDB();
        User savedAuthor = userRepository.save(author);
        newArticle.getTags().add(newTag1);
        ArticleFullDto savedArticle = articleService.createArticle(savedAuthor.getUserId(), newArticle);

        ArticleFullDto result = tagService.addTagsToArticle(
                savedAuthor.getUserId(), savedArticle.getArticleId(), new ArrayList<>());
        List<TagShortDto> tags = result.getTags().stream().toList();

        assertThat(result).isNotNull();
        assertThat(result.getTags().size()).isEqualTo(1);
        assertThat(tags.get(0).getName()).isEqualTo(newTag1.getName());
        assertThat(tagRepository.findAll().size()).isEqualTo(1);
    }

    private void dropDB() {
        tagRepository.deleteAll();
        articleRepository.deleteAll();
        userRepository.deleteAll();
    }
}
