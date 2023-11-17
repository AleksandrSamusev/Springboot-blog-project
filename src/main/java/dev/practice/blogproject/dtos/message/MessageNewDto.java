package dev.practice.blogproject.dtos.message;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageNewDto {

    @NotBlank(message = "Message cannot be blank")
    @Length(max = 500, message = "Message length should be 500 chars max")
    private String message;

}
