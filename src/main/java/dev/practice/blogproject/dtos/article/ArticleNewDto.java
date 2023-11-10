package dev.practice.blogproject.dtos.article;

import dev.practice.blogproject.dtos.tag.TagNewDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleNewDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
    private Set<TagNewDto> tags;

}
