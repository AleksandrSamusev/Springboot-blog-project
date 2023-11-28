package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.mappers.ArticleMapper;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.ArticleStatus;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.services.ArticleAdminService;
import dev.practice.mainApp.services.ArticlePrivateService;
import dev.practice.mainApp.utils.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleAdminServiceImpl implements ArticleAdminService {
    private final ArticleRepository articleRepository;
    private final ArticlePrivateService articleService;
    private final Validations validations;

    @Override
    public List<ArticleFullDto> getAllArticlesByUserId(String login, Long authorId, Integer from,
                                                       Integer size, String status) {
        User user = validations.checkUserExistsByUsernameOrEmail(login);
        if (!validations.isAdmin(login)) {
            log.error("Action forbidden. User with id {} is not admin", user.getUserId());
            throw new ActionForbiddenException(String.format("Action forbidden. User with id %d is not admin",
                    user.getUserId()));
        }
        User author = validations.checkUserExist(authorId);
        return articleService.getAllArticlesByUserId(author.getUsername(), from, size, status);
    }

    @Override
    public ArticleFullDto publishArticle(String login, Long articleId, boolean publish) {
        User user = validations.checkUserExistsByUsernameOrEmail(login);
        if (!validations.isAdmin(login)) {
            log.error("Action forbidden. User with id {} is not admin", user.getUserId());
            throw new ActionForbiddenException(String.format("Action forbidden. User with id %d is not admin",
                    user.getUserId()));
        }
        Article article = validations.checkArticleExist(articleId);

        if (publish) {
            article.setPublished(LocalDateTime.now());
            article.setStatus(ArticleStatus.PUBLISHED);

            log.info("Article with id {} was published at {}. Admin id is {}",
                    articleId, article.getPublished(), user.getUserId());
            return ArticleMapper.toArticleFullDto(articleRepository.save(article));
        }

        article.setStatus(ArticleStatus.REJECTED);
        log.info("Article with id {} was REJECTED. Admin id is {}", articleId, user.getUserId());
        return ArticleMapper.toArticleFullDto(articleRepository.save(article));
    }
}
