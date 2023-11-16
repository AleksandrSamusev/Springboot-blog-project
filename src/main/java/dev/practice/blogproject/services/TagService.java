package dev.practice.blogproject.services;

import dev.practice.blogproject.dtos.tag.TagFullDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;

import java.util.List;

public interface TagService {
    TagFullDto createTag(TagNewDto dto, Long articleId);

    void deleteTag(Long tagId, Long userId);

    List<TagFullDto> getAllArticleTags(Long articleId);

    TagFullDto getTagById(Long tagId);
}
