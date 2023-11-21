package dev.practice.mainApp.dtos.tag;

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
public class TagNewDto {

    @NotBlank(message = "Tag name cannot be blank")
    @Length(max = 50, message = "Name length should be 50 chars max")
    private String name;

}
