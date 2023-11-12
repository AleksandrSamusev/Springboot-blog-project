package dev.practice.blogproject.services;

import dev.practice.blogproject.dtos.tag.TagFullDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;

public interface TagService {
    TagFullDto createTag(TagNewDto dto, Long articleId, Long userId);

    void deleteTag(Long tagId, Long userId);

}
