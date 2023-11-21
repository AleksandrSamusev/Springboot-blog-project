package dev.practice.mainApp.dtos.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagShortDto {

    @NotNull(message = "Tag ID cannot be null")
    private Long tagId;

    @NotBlank(message = "Tag name cannot be blank")
    String name;

}
