package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.TagFullDto;
import dev.practice.blogproject.models.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class TagMapper {

    public static Tag toTag(TagFullDto dto) {
        return new Tag(
                dto.getTagId(),
                dto.getName(),
                dto.getArticles()
        );
    }

    public static TagFullDto toTagDto(Tag tag) {
        return new TagFullDto(
                tag.getTagId(),
                tag.getName(),
                tag.getArticles()
        );
    }

    public static List<Tag> toTags(List<TagFullDto> dtos) {
        return dtos.stream().map(TagMapper::toTag).collect(Collectors.toList());
    }

    public static List<TagFullDto> toTagDtos(List<Tag> tags) {
        return tags.stream().map(TagMapper::toTagDto).collect(Collectors.toList());
    }
}
