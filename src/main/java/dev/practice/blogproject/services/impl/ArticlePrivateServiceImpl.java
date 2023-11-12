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
import dev.practice.blogproject.services.TagService;
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
    private final TagService tagService;

    @Override
    public ArticleFullDto createArticle(long userId, ArticleNewDto newArticle) {
        checkUserExist(userId);

        long articleId = articleRepository.save(ArticleMapper.toArticleFromNew(newArticle, userId)).getArticleId();
        if (newArticle.getTags() != null && !newArticle.getTags().isEmpty()) {
            checkTagExist(newArticle.getTags(), articleId, userId);
        }

        return ArticleMapper.toArticleFullDto(articleRepository.getReferenceById(articleId));
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

    private void checkTagExist(Set<TagNewDto> tags, long articleId, long userId) {
        for (TagNewDto newTag : tags) {
            Tag tag = tagRepository.findTagByName(newTag.getName());
            if (tag != null) {
                tag.getArticles().add(new Article(articleId));
                tagRepository.save(tag);
                log.info("Tag with id {} was added to article with id {}", tag.getTagId(), articleId);
            } else {
                tagService.createTag(newTag, articleId, userId);
            }
        }
    }
}
