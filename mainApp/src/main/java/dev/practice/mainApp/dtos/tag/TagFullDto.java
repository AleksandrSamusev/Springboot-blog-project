package dev.practice.mainApp.dtos.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Tag ID cannot be null")
    private Long tagId;

    @NotBlank(message = "Tag name cannot be blank")
    private String name;
    private Set<Long> articles;

}
