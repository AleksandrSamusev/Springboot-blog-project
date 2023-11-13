package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.dtos.article.ArticleNewDto;
import dev.practice.blogproject.dtos.article.ArticleUpdateDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.mappers.ArticleMapper;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.Tag;
import dev.practice.blogproject.models.User;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.TagRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.ArticlePrivateService;
import dev.practice.blogproject.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
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
        User user = checkUserExist(userId);
        checkUserIsNotBanned(user);
        checkTitleExist(newArticle.getTitle());

        long articleId = articleRepository.save(ArticleMapper.toArticleFromNew(newArticle, user)).getArticleId();
        if (newArticle.getTags() != null && !newArticle.getTags().isEmpty()) {
            Set<Tag> tags = (checkTagExist(newArticle.getTags(), articleId));
            if (!tags.isEmpty()) {
                Article article = articleRepository.getReferenceById(articleId);
                tags.addAll(article.getTags());
                article.setTags(tags);
                return ArticleMapper.toArticleFullDto(articleRepository.save(article));
            }
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

    private User checkUserExist(long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.error("User with id {} wasn't found", userId);
            throw new ResourceNotFoundException(String.format("User with id %d wasn't found", userId));
        }
        return user.get();
    }

    private void checkUserIsNotBanned(User user) {
        if (user.getIsBanned()) {
            log.error("User with id {} is blocked", user.getUserId());
            throw new ActionForbiddenException(String.format("User with id %d is blocked", user.getUserId()));
        }
    }

    private void checkTitleExist(String title) {
        if (articleRepository.findArticlesByTitleIgnoreCase(title) != null) {
            log.error("Article with title {} already exist", title);
            throw new InvalidParameterException(String.format("Article with title %s already exist", title));
        }
    }

    private Set<Tag> checkTagExist(Set<TagNewDto> tags, long articleId) {
        Set<Tag> allTags = new HashSet<>();
        for (TagNewDto newTag : tags) {
            Tag tag = tagRepository.findTagByName(newTag.getName());
            if (tag != null) {
                allTags.add(tag);
                Set<Article> articles = tag.getArticles();
                articles.add(articleRepository.getReferenceById(articleId));
                tag.setArticles(articles);
                tagRepository.save(tag);
                log.info("Tag with id {} was added to article with id {}", tag.getTagId(), articleId);
            } else {
                tagService.createTag(newTag, articleId);
            }
        }
        return allTags;
    }
}
