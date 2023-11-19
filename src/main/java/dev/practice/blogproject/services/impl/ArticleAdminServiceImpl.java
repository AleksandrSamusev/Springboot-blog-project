package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.mappers.ArticleMapper;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.ArticleStatus;
import dev.practice.blogproject.models.Role;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.ArticleAdminService;
import dev.practice.blogproject.services.ArticlePrivateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleAdminServiceImpl implements ArticleAdminService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticlePrivateService articleService;

    @Override
    public List<ArticleFullDto> getAllArticlesByUserId(Long userId, Long authorId, Integer from,
                                                       Integer size, String status) {
        checkUserIsAdmin(userId);
        return articleService.getAllArticlesByUserId(authorId, from, size, status);
    }

    @Override
    public ArticleFullDto publishArticle(Long userId, Long articleId, boolean publish) {
        checkUserIsAdmin(userId);
        Article article = checkArticleExist(articleId);

        if (publish) {
            article.setPublished(LocalDateTime.now());
            article.setStatus(ArticleStatus.PUBLISHED);

            log.info("Article with id {} was published at {}. Admin id is {}",
                    articleId, article.getPublished(), userId);
            return ArticleMapper.toArticleFullDto(articleRepository.save(article));
        }

        article.setStatus(ArticleStatus.REJECTED);
        log.info("Article with id {} was REJECTED. Admin id is {}", articleId, userId);
        return ArticleMapper.toArticleFullDto(articleRepository.save(article));
    }

    private void checkUserIsAdmin(Long userId) {
        if (userRepository.findById(userId).get().getRole() != Role.ADMIN) {
            log.error("User with id {} is not ADMIN", userId);
            throw new ActionForbiddenException(String.format(
                    "User with id %d is not ADMIN. Access is forbidden", userId));
        }
    }

    private Article checkArticleExist(Long articleId) {
        Optional<Article> article = articleRepository.findById(articleId);
        if (article.isEmpty()) {
            log.error("Article with id {} wasn't found", articleId);
            throw new ResourceNotFoundException(String.format("Article with id %d wasn't found", articleId));
        }
        return article.get();
    }

}
