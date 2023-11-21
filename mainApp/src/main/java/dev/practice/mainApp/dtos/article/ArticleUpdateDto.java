package dev.practice.mainApp.dtos.article;

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

    @Length(max = 250, message = "Title length should be 250 chars max")
    private String title;

    @Length(max = 30000, message = "Content length should be 30000 chars max")
    private String content;

}
