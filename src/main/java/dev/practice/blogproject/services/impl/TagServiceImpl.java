package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.tag.TagFullDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.mappers.TagMapper;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.Tag;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.TagRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.TagService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Override
    public TagFullDto createTag(TagNewDto dto, Long articleId, Long userId) {
        isValidParameters(dto, articleId, userId);
        Article article = articleRepository.findById(articleId).orElseThrow(() ->
                new ResourceNotFoundException("article with given ID = " + articleId + " not found"));
        if (!article.getAuthor().getUserId().equals(userId)) {
            throw new ActionForbiddenException("Action forbidden for current user");
        } else {
            Tag tag = TagMapper.toTag(dto);
            tag.getArticles().add(article);
            Tag savedTag = tagRepository.save(tag);
            log.info("Tag with ID = " + savedTag.getTagId() + " created");
            return TagMapper.toTagDto(savedTag);
        }
    }

    @Override
    public void deleteTag(Long tagId, Long userId) {
        isValidParameters(tagId, userId);
        isUserPresent(userId);
        isAdmin(userId);
        isTagPresent(tagId);
        tagRepository.deleteById(tagId);
        log.info("Tag with ID = " + tagId + " successfully deleted");
    }


    private void isAdmin(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            if (!userRepository.findById(userId).get().getRole().name().equals("ADMIN")) {
                throw new ActionForbiddenException("Action forbidden for current user");
            }
        }
    }

    private void isUserPresent(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User with given ID = " + userId + " not found");
        }
    }

    private void isTagPresent(Long tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new ResourceNotFoundException("Tag with given ID = " + tagId + " not found");
        }
    }

    private void isValidParameters(Long tagId, Long userId) {
        if (tagId == null) {
            throw new InvalidParameterException("tagId parameter cannot be NULL");
        } else if (userId == null) {
            throw new InvalidParameterException("userId parameter cannot be NULL");
        }
    }

    private void isValidParameters(TagNewDto dto, Long articleId, Long userId) {
        if (dto == null) {
            throw new InvalidParameterException("dto parameter cannot be NULL");
        } else if (articleId == null) {
            throw new InvalidParameterException("articleId parameter cannot be NULL");
        } else if (userId == null) {
            throw new InvalidParameterException("userId parameter cannot be NULL");
        }
    }
}
