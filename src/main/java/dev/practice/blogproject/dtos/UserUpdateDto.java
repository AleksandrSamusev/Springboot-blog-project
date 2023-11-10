package dev.practice.blogproject.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    String firstName;
    String lastName;
    String username;

    @Email
    String email;

    @Past
    LocalDateTime birthDate;
    String about;
}
