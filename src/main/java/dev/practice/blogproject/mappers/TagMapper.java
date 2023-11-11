package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.tag.TagFullDto;
import dev.practice.blogproject.dtos.tag.TagShortDto;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class TagMapper {

    public static TagFullDto toTagDto(Tag tag) {
        return new TagFullDto(
                tag.getTagId(),
                tag.getName(),
                tag.getArticles().stream().map(Article::getArticleId).collect(Collectors.toSet())
        );
    }

    public static TagShortDto toTagShortDto(Tag tag) {
        return new TagShortDto(tag.getTagId(),
                tag.getName());
    }

    public static List<TagFullDto> toTagDtos(List<Tag> tags) {
        return tags.stream().map(TagMapper::toTagDto).collect(Collectors.toList());
    }
}
