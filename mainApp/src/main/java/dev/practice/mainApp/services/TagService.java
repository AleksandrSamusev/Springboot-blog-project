package dev.practice.mainApp.services;

import dev.practice.mainApp.dtos.article.ArticleFullDto;
import dev.practice.mainApp.dtos.tag.TagFullDto;
import dev.practice.mainApp.dtos.tag.TagNewDto;

import java.util.List;

public interface TagService {
    TagFullDto createTag(TagNewDto dto, Long articleId);

    void deleteTag(Long tagId, Long userId);

    List<TagFullDto> getAllArticleTags(Long articleId);

    TagFullDto getTagById(Long tagId);

    ArticleFullDto addTagsToArticle(Long userId, Long articleId, List<TagNewDto> tags);

    ArticleFullDto removeTagsFromArticle(Long userId, Long articleId, List<Long> tags);
}
