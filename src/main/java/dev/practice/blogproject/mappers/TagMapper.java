package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.TagDto;
import dev.practice.blogproject.models.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class TagMapper {

    public static Tag toTag(TagDto dto) {
        return new Tag(
                dto.getTagId(),
                dto.getName(),
                dto.getArticles()
        );
    }

    public static TagDto toTagDto(Tag tag) {
        return new TagDto(
                tag.getTagId(),
                tag.getName(),
                tag.getArticles()
        );
    }

    public static List<Tag> toTags(List<TagDto> dtos) {
        return dtos.stream().map(TagMapper::toTag).collect(Collectors.toList());
    }

    public static List<TagDto> toTagDtos(List<Tag> tags) {
        return tags.stream().map(TagMapper::toTagDto).collect(Collectors.toList());
    }
}
