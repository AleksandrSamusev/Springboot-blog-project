package dev.practice.mainApp.article;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.article.ArticleNewDto;
import dev.practice.mainApp.dtos.article.ArticleUpdateDto;
import dev.practice.mainApp.dtos.comment.CommentNewDto;
import dev.practice.mainApp.dtos.tag.TagNewDto;
import dev.practice.mainApp.dtos.tag.TagShortDto;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.models.*;
import dev.practice.mainApp.repositories.*;
import dev.practice.mainApp.services.ArticlePrivateService;
import dev.practice.mainApp.services.CommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


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
    private final CommentService commentService;
    private final CommentRepository commentRepository;
    private final RoleRepository roleRepository;

    @MockBean
    protected AuthenticationConfiguration authenticationConfiguration;
    @MockBean
    protected AuthenticationManager authenticationManager;
    @MockBean
    protected HttpSecurity httpSecurity;
    @MockBean
    protected SecurityFilterChain securityFilterChain;


    private final User user = new User(null, "Harry", "Potter", "HP",
            "password", "hp@gmail.com", LocalDate.of(1981, 7, 31),
            new HashSet<>(), null, false, new HashSet<>(), new HashSet<>(), new HashSet<>(),
            new HashSet<>());
    private final User user2 = new User(null, "Admin", "Admin", "ADMIN",
            "password", "admin@gmail.com", LocalDate.of(1990, 9, 10),
            new HashSet<>(), null, false, new HashSet<>(), new HashSet<>(), new HashSet<>(),
            new HashSet<>());
    private final Tag tag1 = new Tag(null, "Potions", new HashSet<>());
    private final Tag tag2 = new Tag(null, "Cat", new HashSet<>());
    private final ArticleNewDto newArticle = new ArticleNewDto("The empty pot",
            "Very interesting information", new HashSet<>());
    private final ArticleNewDto newArticle2 = new ArticleNewDto("Pot", "Interesting information",
            new HashSet<>());
    private final Article article = new Article(null, "The empty pot",
            "Very interesting information", user, LocalDateTime.now(), LocalDateTime.now().minusDays(5),
            ArticleStatus.PUBLISHED, 1450L, 0L, new HashSet<>(), new HashSet<>());
    private final Article article2 = new Article(null, "A pretty cat",
            "Very interesting information", user, LocalDateTime.now(), null, ArticleStatus.CREATED,
            0L, 0L, new HashSet<>(), new HashSet<>());


    void setRoles() {
        if(roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            Role roleAdmin = new Role(null, "ROLE_ADMIN");
            roleRepository.save(roleAdmin);
        }
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            Role roleUser = new Role(null, "ROLE_USER");
            roleRepository.save(roleUser);
        }
    }

    @Test
    void articlePr_test_13_Given_validArticleWithNewTags_When_createArticle_Then_articleSavedWithTag() {
        setRoles();
        dropDB();
        newArticle.getTags().add(new TagNewDto(tag1.getName()));
        User author = userRepository.save(user);

        ArticleFullDto result = articleService.createArticle(author.getUsername(), newArticle);

        List<TagShortDto> tags = result.getTags().stream().toList();
        Tag tag = tagRepository.findTagByName(tag1.getName().toLowerCase());

        assertThat(result).isNotNull();
        assertThat(tags.size()).isEqualTo(1);
        assertThat(tags.get(0).getName()).isEqualTo(tag1.getName().toLowerCase());
        assertThat(tagRepository.getReferenceById(tag.getTagId()).getArticles().size()).isEqualTo(1);
    }

    @Test
    void article_test_3_Given_validArticleWithExistTag_When_createArticle_Then_articleSavedWithTag() {
        dropDB();
        newArticle.getTags().add(new TagNewDto(tag1.getName()));
        newArticle2.getTags().add(new TagNewDto(tag1.getName()));
        User author = userRepository.save(user);
        articleService.createArticle(author.getUsername(), newArticle);

        ArticleFullDto result = articleService.createArticle(author.getUsername(), newArticle2);

        List<TagShortDto> tags = result.getTags().stream().toList();
        Tag tag = tagRepository.findTagByName(tag1.getName().toLowerCase());

        assertThat(result).isNotNull();
        assertThat(tags.size()).isEqualTo(1);
        assertThat(tags.get(0).getName()).isEqualTo(tag1.getName().toLowerCase());
        assertThat(tagRepository.getReferenceById(tag.getTagId()).getArticles().size()).isEqualTo(2);
    }

    @Test
    void article_test_4_Given_validArticleWithExistAndNewTags_When_createArticle_Then_articleSavedWithTags() {
        dropDB();
        newArticle.getTags().add(new TagNewDto(tag1.getName()));
        newArticle2.getTags().add(new TagNewDto(tag1.getName()));
        newArticle2.getTags().add(new TagNewDto(tag2.getName()));
        User author = userRepository.save(user);
        articleService.createArticle(author.getUsername(), newArticle);

        ArticleFullDto result = articleService.createArticle(author.getUsername(), newArticle2);

        List<TagShortDto> tags = result.getTags().stream().sorted(Comparator.comparing(TagShortDto::getName)).toList();
        Tag tag = tagRepository.findTagByName(tag1.getName().toLowerCase());
        Tag tag3 = tagRepository.findTagByName(tag2.getName().toLowerCase());

        assertThat(result).isNotNull();
        assertThat(tags.size()).isEqualTo(2);
        assertThat(tags.get(1).getName()).isEqualTo(tag1.getName().toLowerCase());
        assertThat(tags.get(0).getName()).isEqualTo(tag2.getName().toLowerCase());
        assertThat(tagRepository.getReferenceById(tag.getTagId()).getArticles().size()).isEqualTo(2);
        assertThat(tagRepository.getReferenceById(tag3.getTagId()).getArticles().size()).isEqualTo(1);
    }

    @Test
    void article_test_5_Given_articleWithTitleAlreadyExist_When_createArticle_Then_throwException() {
        dropDB();
        User author = userRepository.save(user);
        articleService.createArticle(author.getUsername(), newArticle);


        final InvalidParameterException exception = Assertions.assertThrows(InvalidParameterException.class,
                () -> articleService.createArticle(author.getUsername(), newArticle));
        assertEquals("Article with title The empty pot already exist", exception.getMessage(),
                "Incorrect message");
        assertThat(exception).isInstanceOf(InvalidParameterException.class);
    }

    @Test
    void article_test_9_Given_validNewTitle_When_updateArticle_Then_articleUpdatedCorrectly() {
        dropDB();
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

        ArticleFullDto result = articleService.updateArticle(author.getUsername(), article.getArticleId(), update);

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
        dropDB();
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

        ArticleFullDto result = articleService.updateArticle(author.getUsername(), article.getArticleId(), update);

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
        dropDB();
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

        ArticleFullDto result = articleService.updateArticle(author.getUsername(), article.getArticleId(), update);

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
        dropDB();
        User author = userRepository.save(user);
        articleRepository.save(article);
        Article articleSaved2 = articleRepository.save(article2);
        ArticleUpdateDto update = new ArticleUpdateDto();
        update.setTitle("THE EMPTY POT   ");

        final InvalidParameterException exception = Assertions.assertThrows(InvalidParameterException.class,
                () -> articleService.updateArticle(author.getUsername(), articleSaved2.getArticleId(), update));
        assertEquals("Article with title THE EMPTY POT    already exist",
                exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(InvalidParameterException.class);
    }

    @Test
    void article_test_24_Given_validIds_When_deleteArticle_Then_articleDeleted() {
        dropDB();
        newArticle.getTags().add(new TagNewDto(tag1.getName()));
        newArticle.getTags().add(new TagNewDto(tag2.getName()));
        User author = userRepository.save(user);
        ArticleFullDto articleSaved = articleService.createArticle(author.getUsername(), newArticle);
        Article dbArticle = articleRepository.getReferenceById(articleSaved.getArticleId());
        dbArticle.setStatus(ArticleStatus.PUBLISHED);
        articleRepository.save(dbArticle);

        commentService.createComment(articleSaved.getArticleId(), new CommentNewDto("comment"), author.getUsername());

        articleService.deleteArticle(author.getUsername(), articleSaved.getArticleId());

        assertThat(articleRepository.findAll().size()).isEqualTo(0);
        assertThat(tagRepository.findAll().size()).isEqualTo(2);
        assertThat(tagRepository.findAll().get(0).getArticles().size()).isEqualTo(0);
        assertThat(tagRepository.findAll().get(1).getArticles().size()).isEqualTo(0);
        assertThat(commentRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void article_test_25_Given_adminAndValidArticleId_When_deleteArticle_Then_articleDeleted() {
        dropDB();
        setRoles();
        newArticle.getTags().add(new TagNewDto(tag1.getName()));
        newArticle.getTags().add(new TagNewDto(tag2.getName()));
        User author = userRepository.save(user);
        user2.setRoles(Set.of(roleRepository.findByName("ROLE_ADMIN").get()));
        User admin = userRepository.save(user2);
        ArticleFullDto articleSaved = articleService.createArticle(author.getUsername(), newArticle);
        Article dbArticle = articleRepository.getReferenceById(articleSaved.getArticleId());
        dbArticle.setStatus(ArticleStatus.PUBLISHED);
        articleRepository.save(dbArticle);
        commentService.createComment(articleSaved.getArticleId(), new CommentNewDto("comment"), author.getUsername());

        articleService.deleteArticle(admin.getUsername(), articleSaved.getArticleId());

        assertThat(articleRepository.findAll().size()).isEqualTo(0);
        assertThat(tagRepository.findAll().size()).isEqualTo(2);
        assertThat(tagRepository.findAll().get(0).getArticles().size()).isEqualTo(0);
        assertThat(tagRepository.findAll().get(1).getArticles().size()).isEqualTo(0);
        assertThat(commentRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void article_test_28_Given_userIdDefaultSize_When_getAllArticlesByUserId_Then_return10newestArticlesOfUser() {
        dropDB();
        User author = userRepository.save(user);
        for (int i = 0; i < 20; i++) {
            articleRepository.save(new Article(null, String.valueOf(i), "some information", author,
                    LocalDateTime.now(), null, ArticleStatus.CREATED, 0L, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        author.getArticles().addAll(articleRepository.findAll());
        userRepository.save(author);

        List<ArticleFullDto> result = articleService.getAllArticlesByUserId(author.getUsername(), 0, 10,
                "ALL");

        assertThat(articleRepository.findAll().size()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.get(0).getAuthor().getUserId()).isEqualTo(author.getUserId());
    }

    @Test
    void article_test_29_Given_userIdSize5from3_When_getAllArticlesByUserId_Then_return5ArticlesOfUser() {
        dropDB();
        User author = userRepository.save(user);
        for (int i = 0; i < 20; i++) {
            articleRepository.save(new Article(null, String.valueOf(i), "some information", author,
                    LocalDateTime.now(), null, ArticleStatus.CREATED, 0L, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        author.getArticles().addAll(articleRepository.findAll());
        userRepository.save(author);

        List<ArticleFullDto> result = articleService.getAllArticlesByUserId(author.getUsername(), 10, 5,
                "ALL");

        assertThat(articleRepository.findAll().size()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.get(0).getAuthor().getUserId()).isEqualTo(author.getUserId());
        assertThat(result.get(0).getTitle()).isEqualTo("4");
        assertThat(result.get(4).getTitle()).isEqualTo("0");
    }

    @Test
    void article_test_30_Given_userIdStatusPublished_When_getAllArticlesByUserId_Then_return10PublishedArticlesOfUser() {
        dropDB();
        User author = userRepository.save(user);
        for (int i = 0; i < 20; i++) {
            articleRepository.save(new Article(null, String.valueOf(i), "some information", author,
                    LocalDateTime.now(), null, ArticleStatus.CREATED, 0L, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        for (int i = 0; i < 5; i++) {
            articleRepository.save(new Article(null, String.valueOf(i), "some information", author,
                    LocalDateTime.now(), LocalDateTime.now(), ArticleStatus.PUBLISHED, 0L, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        author.getArticles().addAll(articleRepository.findAll());
        userRepository.save(author);

        List<ArticleFullDto> result = articleService.getAllArticlesByUserId(author.getUsername(), 0, 10,
                "PUBLISHED");

        assertThat(articleRepository.findAll().size()).isEqualTo(25);
        assertThat(result.size()).isEqualTo(5);
        assertThat(result.get(0).getAuthor().getUserId()).isEqualTo(author.getUserId());
        assertThat(result.get(0).getStatus()).isEqualTo(ArticleStatus.PUBLISHED);
    }

    @Test
    void article_test_31_Given_userIdStatusRejected_When_getAllArticlesByUserId_Then_return10RejectedArticlesOfUser() {
        dropDB();
        User author = userRepository.save(user);
        for (int i = 0; i < 20; i++) {
            articleRepository.save(new Article(null, String.valueOf(i), "some information", author,
                    LocalDateTime.now(), null, ArticleStatus.CREATED, 0L, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        for (int i = 0; i < 5; i++) {
            articleRepository.save(new Article(null, String.valueOf(i), "some information", author,
                    LocalDateTime.now(), LocalDateTime.now(), ArticleStatus.PUBLISHED, 0L, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        articleRepository.save(new Article(null, "r", "some information", author,
                LocalDateTime.now(), null, ArticleStatus.REJECTED, 0L, 0L, new HashSet<>(),
                new HashSet<>()));
        author.getArticles().addAll(articleRepository.findAll());
        userRepository.save(author);

        List<ArticleFullDto> result = articleService.getAllArticlesByUserId(author.getUsername(), 0, 10,
                "REJECTED");

        assertThat(articleRepository.findAll().size()).isEqualTo(26);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getAuthor().getUserId()).isEqualTo(author.getUserId());
        assertThat(result.get(0).getStatus()).isEqualTo(ArticleStatus.REJECTED);
    }

    @Test
    void article_test_32_Given_userIdStatusModerating_When_getAllArticlesByUserId_Then_return10ModeratingArticlesOfUser() {
        dropDB();
        User author = userRepository.save(user);
        for (int i = 0; i < 20; i++) {
            articleRepository.save(new Article(null, String.valueOf(i), "some information", author,
                    LocalDateTime.now(), null, ArticleStatus.CREATED, 0L, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        for (int i = 0; i < 5; i++) {
            articleRepository.save(new Article(null, String.valueOf(i), "some information", author,
                    LocalDateTime.now(), LocalDateTime.now(), ArticleStatus.PUBLISHED, 0L, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        articleRepository.save(new Article(null, "r", "some information", author,
                LocalDateTime.now(), null, ArticleStatus.MODERATING, 0L, 0L, new HashSet<>(),
                new HashSet<>()));
        author.getArticles().addAll(articleRepository.findAll());
        userRepository.save(author);

        List<ArticleFullDto> result = articleService.getAllArticlesByUserId(author.getUsername(), 0, 10,
                "MODERATING");

        assertThat(articleRepository.findAll().size()).isEqualTo(26);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getAuthor().getUserId()).isEqualTo(author.getUserId());
        assertThat(result.get(0).getStatus()).isEqualTo(ArticleStatus.MODERATING);
    }

    @Test
    void article_test_33_Given_userIdStatusCreated_When_getAllArticlesByUserId_Then_return10CreatedArticlesOfUser() {
        dropDB();
        User author = userRepository.save(user);
        for (int i = 0; i < 20; i++) {
            articleRepository.save(new Article(null, String.valueOf(i), "some information", author,
                    LocalDateTime.now(), null, ArticleStatus.CREATED, 0L, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        for (int i = 0; i < 5; i++) {
            articleRepository.save(new Article(null, String.valueOf(i), "some information", author,
                    LocalDateTime.now(), LocalDateTime.now(), ArticleStatus.PUBLISHED, 0L, 0L, new HashSet<>(),
                    new HashSet<>()));
        }
        articleRepository.save(new Article(null, "r", "some information", author,
                LocalDateTime.now(), null, ArticleStatus.MODERATING, 0L, 0L, new HashSet<>(),
                new HashSet<>()));
        author.getArticles().addAll(articleRepository.findAll());
        userRepository.save(author);

        List<ArticleFullDto> result = articleService.getAllArticlesByUserId(author.getUsername(), 0, 10,
                "CREATED");

        assertThat(articleRepository.findAll().size()).isEqualTo(26);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.get(0).getAuthor().getUserId()).isEqualTo(author.getUserId());
        assertThat(result.get(0).getStatus()).isEqualTo(ArticleStatus.CREATED);
    }

    @Test
    void article_test_34_Given_userIdUnsupportedStatus_When_getAllArticlesByUserId_Then_throwException() {
        dropDB();
        User author = userRepository.save(user);

        final InvalidParameterException exception = Assertions.assertThrows(InvalidParameterException.class,
                () -> articleService.getAllArticlesByUserId(author.getUsername(), 0, 10, "UNSUPPORTED"));
        assertEquals("Unknown status: UNSUPPORTED", exception.getMessage(), "Incorrect message");
        assertThat(exception).isInstanceOf(InvalidParameterException.class);
    }

    @Test
    void article_test37_Given_articleWithComments_When_updateArticle_Then_commentsDeleted() {
        dropDB();
        User author = userRepository.save(user);
        Article articleSaved = articleRepository.save(article);
        Comment comment = commentRepository.save(new Comment(null, "comment", LocalDateTime.now(),
                articleSaved, author));
        ArticleUpdateDto update = new ArticleUpdateDto();
        update.setContent("new content");
        articleSaved.getComments().add(comment);
        articleRepository.save(articleSaved);

        ArticleFullDto result = articleService.updateArticle(author.getUsername(), articleSaved.getArticleId(), update);

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
        assertThat(result.getTags().size()).isEqualTo(0);
        assertThat(commentRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    void article_test38_Given_articleWithComments_When_updateArticle_Then_commentsInOtherArticlesNotDeleted() {
        dropDB();
        User author = userRepository.save(user);
        Article articleSaved = articleRepository.save(article);
        article2.setStatus(ArticleStatus.PUBLISHED);
        article2.setPublished(LocalDateTime.now());
        Article articleSaved2 = articleRepository.save(article2);
        Comment comment = commentRepository.save(new Comment(null, "comment", LocalDateTime.now(),
                articleSaved, author));
        Comment comment2 = commentRepository.save(new Comment(null, "comment", LocalDateTime.now(),
                articleSaved2, author));
        ArticleUpdateDto update = new ArticleUpdateDto();
        update.setContent("new content");
        articleSaved.getComments().add(comment);
        articleRepository.save(articleSaved);
        articleSaved2.getComments().add(comment2);
        articleRepository.save(articleSaved2);

        ArticleFullDto result = articleService.updateArticle(author.getUsername(), articleSaved.getArticleId(), update);

        assertThat(result).isNotNull();
        assertThat(result.getArticleId()).isEqualTo(articleSaved.getArticleId());
        assertThat(result.getComments().size()).isEqualTo(0);
        assertThat(commentRepository.findAll().size()).isEqualTo(1);
    }

    private void dropDB() {
        commentRepository.deleteAll();
        tagRepository.deleteAll();
        articleRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();
    }
}
