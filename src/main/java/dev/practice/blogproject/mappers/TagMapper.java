package dev.practice.blogproject.mappers;

import dev.practice.blogproject.dtos.tag.TagFullDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.dtos.tag.TagShortDto;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.Tag;
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

    public static Tag toTag(TagNewDto dto) {
        Tag tag = new Tag();
        tag.setName(dto.getName());
        return tag;
    }
}
