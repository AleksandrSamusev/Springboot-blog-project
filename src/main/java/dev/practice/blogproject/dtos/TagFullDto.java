package dev.practice.blogproject.dtos;

import dev.practice.blogproject.models.Article;
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
public class TagFullDto {
    private Long tagId;

    @NotBlank
    private String name;
    private Set<Long> articles;
}
