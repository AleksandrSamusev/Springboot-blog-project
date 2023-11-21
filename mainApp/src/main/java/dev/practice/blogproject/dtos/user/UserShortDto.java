package dev.practice.blogproject.dtos.user;

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
public class UserShortDto {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotBlank(message = "Username cannot be blank")
    private String username;

}
