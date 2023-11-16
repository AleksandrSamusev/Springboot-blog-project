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

import java.util.List;
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
        isValidParameters(articleId);
        Article article = articleRepository.findById(articleId).orElseThrow(() ->
                new ResourceNotFoundException("article with given ID = " + articleId + " not found"));

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
        isValidParameters(tagId);
        isUserExists(userId);
        isAdmin(userId);
        isTagExists(tagId);
        tagRepository.deleteById(tagId);
        log.info("Tag with ID = " + tagId + " successfully deleted");
    }

    @Override
    public List<TagFullDto> getAllArticleTags(Long articleId) {
        isArticleExists(articleId);
        return articleRepository.findById(articleId).get().getTags()
                .stream().map(TagMapper::toTagFullDto).collect(Collectors.toList());
    }

    @Override
    public TagFullDto getTagById(Long tagId) {
        isTagExists(tagId);
        return TagMapper.toTagFullDto(tagRepository.findById(tagId).get());
    }


    private void isAdmin(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            if (!userRepository.findById(userId).get().getRole().name().equals("ADMIN")) {
                throw new ActionForbiddenException("Action forbidden for current user");
            }
        }
    }

    private void isUserExists(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User with given ID = " + userId + " not found");
        }
    }

    private void isTagExists(Long tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new ResourceNotFoundException("Tag with given ID = " + tagId + " not found");
        }
    }

    private void isArticleExists(Long articleId) {
        if(articleRepository.existsById(articleId)) {
            throw new ResourceNotFoundException("Article with given ID = " + articleId + " not found");
        }
    }

    private void isValidParameters(Long tagId) {
        if (tagId == null) {
            throw new InvalidParameterException("tagId parameter cannot be NULL");
        }
    }
}
