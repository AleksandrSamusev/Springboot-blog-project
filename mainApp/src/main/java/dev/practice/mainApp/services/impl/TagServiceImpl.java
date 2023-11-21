package dev.practice.mainApp.services.impl;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.tag.TagFullDto;
import dev.practice.mainApp.dtos.tag.TagNewDto;
import dev.practice.mainApp.exceptions.ActionForbiddenException;
import dev.practice.mainApp.exceptions.InvalidParameterException;
import dev.practice.mainApp.exceptions.ResourceNotFoundException;
import dev.practice.mainApp.mappers.ArticleMapper;
import dev.practice.mainApp.mappers.TagMapper;
import dev.practice.mainApp.models.Article;
import dev.practice.mainApp.models.Tag;
import dev.practice.mainApp.models.User;
import dev.practice.mainApp.repositories.ArticleRepository;
import dev.practice.mainApp.repositories.TagRepository;
import dev.practice.mainApp.repositories.UserRepository;
import dev.practice.mainApp.services.TagService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Override
    public TagFullDto createTag(TagNewDto dto, Long articleId) {
        isArticleExists(articleId);
        dto.setName(dto.getName().toLowerCase().trim());
        if (tagRepository.findTagByName(dto.getName()) != null) {
            log.info("InvalidParameterException. Tag with given name = " + dto.getName() + " already exists");
            throw new InvalidParameterException("Tag with given name = " + dto.getName() + " already exists");
        }
        Article article = articleRepository.findById(articleId).get();

        Tag tag = TagMapper.toTag(dto);
        tag.getArticles().add(article);
        Tag savedTag = tagRepository.save(tag);

        article.getTags().add(savedTag);
        articleRepository.save(article);

        log.info("Tag with ID = " + savedTag.getTagId() + " created");
        return TagMapper.toTagFullDto(savedTag);

    }

    @Override
    public void deleteTag(Long tagId, Long userId) {
        isUserExists(userId);
        isAdmin(userId);
        isTagExists(tagId);
        tagRepository.deleteById(tagId);
        log.info("Tag with ID = " + tagId + " successfully deleted");
    }

    @Override
    public List<TagFullDto> getAllArticleTags(Long articleId) {
        isArticleExists(articleId);
        log.info("Return all tags for article with ID = " + articleId);
        return articleRepository.findById(articleId).get().getTags()
                .stream().map(TagMapper::toTagFullDto).collect(Collectors.toList());
    }

    @Override
    public TagFullDto getTagById(Long tagId) {
        isTagExists(tagId);
        log.info("Return tag with ID = " + tagId);
        return TagMapper.toTagFullDto(tagRepository.findById(tagId).get());
    }

    @Override
    public ArticleFullDto addTagsToArticle(Long userId, Long articleId, List<TagNewDto> tags) {
        isUserExists(userId);
        Article article = isArticleExists(articleId);
        checkUserIsAuthor(article, userId);

        if (tags.isEmpty()) {
            log.info("Tags connected to article with id {} wasn't changed. New tags list was empty", articleId);
            return ArticleMapper.toArticleFullDto(article);
        }

        for (TagNewDto newTag : tags) {
            newTag.setName(newTag.getName().trim().toLowerCase());

            Tag tag = tagRepository.findTagByName(newTag.getName());
            if (tag != null) {
                if (!article.getTags().contains(tag)) {
                    tag.getArticles().add(article);
                    tagRepository.save(tag);

                    article.getTags().add(tag);
                    articleRepository.save(article);
                    log.info("Tag with id {} was added to article with id {}", tag.getTagId(), articleId);
                }
            } else {
               createTag(newTag, articleId);
            }
        }

        return ArticleMapper.toArticleFullDto(articleRepository.getReferenceById(articleId));
    }

    @Override
    public ArticleFullDto removeTagsFromArticle(Long userId, Long articleId, List<Long> tags) {
        isUserExists(userId);
        Article article = isArticleExists(articleId);
        checkUserIsAuthor(article, userId);

        if (tags.isEmpty()) {
            log.info("Tags connected to article with id {} wasn't changed. Tags list was empty", articleId);
            return ArticleMapper.toArticleFullDto(article);
        }

        for (Long tagId : tags) {
            Tag tag = isTagExists(tagId);
            tag.getArticles().remove(article);
            tagRepository.save(tag);

            article.getTags().remove(tag);
            log.info("Tags with id {} unconnected from article with id {}", tagId, articleId);
        }

        return ArticleMapper.toArticleFullDto(articleRepository.save(article));
    }


    private void isAdmin(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            if (!userRepository.findById(userId).get().getRole().name().equals("ADMIN")) {
                log.info("ActionForbiddenException. Action forbidden for current user");
                throw new ActionForbiddenException("Action forbidden for current user");
            }
        }
    }

    private User isUserExists(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("ResourceNotFoundException. User with given ID = " + userId + " not found");
            throw new ResourceNotFoundException("User with given ID = " + userId + " not found");
        }
        return user.get();
    }

    private Tag isTagExists(Long tagId) {
        Optional<Tag> tag = tagRepository.findById(tagId);
        if (tag.isEmpty()) {
            log.info("ResourceNotFoundException. Tag with given ID = " + tagId + " not found");
            throw new ResourceNotFoundException("Tag with given ID = " + tagId + " not found");
        }
        return tag.get();
    }

    private Article isArticleExists(Long articleId) {
        Optional<Article> article = articleRepository.findById(articleId);
        if (article.isEmpty()) {
            log.info("ResourceNotFoundException. Article with given ID = " + articleId + " not found");

            throw new ResourceNotFoundException("Article with given ID = " + articleId + " not found");
        }
        return article.get();
    }

    private void checkUserIsAuthor(Article article, long userId) {
        if (article.getAuthor().getUserId() != userId) {
            log.error("Article with id {} is not belongs to user with id {}", article.getArticleId(), userId);
            throw new ActionForbiddenException(String.format(
                    "Article with id %d is not belongs to user with id %d. Action is forbidden",
                    article.getArticleId(), userId));
        }
    }
}
