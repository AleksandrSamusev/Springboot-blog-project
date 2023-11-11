package dev.practice.blogproject.dtos.article;

import dev.practice.blogproject.dtos.tag.TagNewDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleNewDto {

    @NotBlank
    @Length(max = 250)
    private String title;

    @NotBlank
    @Length(max = 30000)
    private String content;
    private Set<TagNewDto> tags;

}
