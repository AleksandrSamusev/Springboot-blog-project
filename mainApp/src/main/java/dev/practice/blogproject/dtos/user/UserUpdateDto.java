package dev.practice.blogproject.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    @Length(max = 50)
    String firstName;

    @Length(max = 50)
    String lastName;

    @Length(max = 50)
    String username;

    @Email
    @Length(max = 50)
    String email;

    @Past
    LocalDate birthDate;

    @Length(max = 1000)
    String about;

}
