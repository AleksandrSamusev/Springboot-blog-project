package dev.practice.mainApp.dtos.article;

import dev.practice.mainApp.dtos.tag.TagNewDto;
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

    @NotBlank(message = "Title cannot be blank")
    @Length(max = 250, message = "Title length should be 250 chars max")
    private String title;

    @NotBlank(message = "Content cannot be blank")
    @Length(max = 30000, message = "Content length should be 30000 chars max")
    private String content;

    private Set<TagNewDto> tags;
}