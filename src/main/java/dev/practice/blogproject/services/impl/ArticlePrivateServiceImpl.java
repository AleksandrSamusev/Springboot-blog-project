package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleUpdateDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.mappers.ArticleMapper;
import dev.practice.blogproject.mappers.TagMapper;
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

import java.util.HashSet;
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
        //checkTitleExist(newArticle.getTitle());

        Article article = articleRepository.save(ArticleMapper.toArticleFromNew(newArticle, userId));
        if (newArticle.getTags() != null && !newArticle.getTags().isEmpty()) {
            article.setTags(checkTagExist(newArticle.getTags(), article.getArticleId(), userId));
        }

        return ArticleMapper.toArticleFullDto(articleRepository.save(article));
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
        if (userRepository.findById(userId).isEmpty()) {
            log.error("User with id {} wasn't found", userId);
            throw new ResourceNotFoundException(String.format("User with id %d wasn't found", userId));
        }
    }

    private void checkTitleExist(String title) {
        if (articleRepository.findArticlesByTitleIgnoreCase(title) != null) {
            log.error("Article with title {} already exist", title);
            throw new InvalidParameterException(String.format("Article with title %s already exist", title));
        }
    }

    private Set<Tag> checkTagExist(Set<TagNewDto> tags, long articleId, long userId) {
        Set<Tag> allTags = new HashSet<>();
        for (TagNewDto newTag : tags) {
            Tag tag = tagRepository.findTagByName(newTag.getName());
            if (tag != null) {
                tag.getArticles().add(new Article(articleId));
                tagRepository.save(tag);
                log.info("Tag with id {} was added to article with id {}", tag.getTagId(), articleId);
            } else {
                allTags.add(TagMapper.toTag(tagService.createTag(newTag, articleId, userId)));
            }
        }
        return allTags;
    }
}
