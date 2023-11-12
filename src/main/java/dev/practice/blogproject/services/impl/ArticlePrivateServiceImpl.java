package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleUpdateDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.mappers.ArticleMapper;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.Tag;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.TagRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.ArticlePrivateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticlePrivateServiceImpl implements ArticlePrivateService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Override
    public ArticleFullDto createArticle(long userId, ArticleNewDto newArticle) {
        checkUserExist(userId);

        Long articleId = articleRepository.save(ArticleMapper.toArticleFromNew(newArticle, userId)).getArticleId();
        if (newArticle.getTags() != null && !newArticle.getTags().isEmpty()) {
            checkTagExist(newArticle.getTags(), articleId);
        }
    }

    @Override
    public ArticleFullDto updateArticle(long userId, long articleId, ArticleUpdateDto updateArticle) {
        return null;
    }

    @Override
    public ArticleFullDto getArticleById(long userId, long articleId) {
        return null;
    }

    @Override
    public void deleteArticle(long userId, long articleId) {

    }

    private void checkUserExist(long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("User with id {} wasn't found", userId);
            throw new ResourceNotFoundException(String.format("Event with id %d wasn't found", userId));
        }
    }

    private Set<Tag> checkTagExist(Set<TagNewDto> tags, long articleId) {
        for (TagNewDto tag : tags) {
            if (tagRepository.)
        }

    }
}
