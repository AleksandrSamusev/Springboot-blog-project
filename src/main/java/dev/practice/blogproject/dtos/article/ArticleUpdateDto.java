package dev.practice.blogproject.dtos.article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticleUpdateDto {

    @Length(max = 250)
    private String title;

    @Length(max = 30000)
    private String content;

}
