package dev.practice.blogproject.dtos;

import dev.practice.blogproject.models.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagDto {
    private Long tagId;
    private String name;
    private Set<Article> articles;
}
