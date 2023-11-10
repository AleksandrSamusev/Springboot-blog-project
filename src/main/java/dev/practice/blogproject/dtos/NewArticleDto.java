package dev.practice.blogproject.dtos;

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
public class NewArticleDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
    private Set<NewTagDto> tags;

}
