package dev.practice.blogproject.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserNewDto {

    @NotBlank(message = "User first name cannot be blank")
    @Length(max = 50, message = "Users first name should be 50 chars max")
    private String firstName;

    @NotBlank(message = "User last name cannot be blank")
    @Length(max = 50, message = "Users last name should be 50 chars max")
    private String lastName;

    @NotBlank(message = "Username cannot be blank")
    @Length(max = 50, message = "Username should be 50 chars max")
    private String username;

    @NotBlank(message = "User email cannot be blank")
    @Email(message = "Incorrect email format")
    @Length(max = 50, message = "Email length should be 50 chars max")
    private String email;

    @NotNull(message = "User birth date cannot be null")
    @Past(message = "Birth date should be in past")
    private LocalDate birthDate;

    @Length(max = 1000, message = "About should be 1000 char max")
    private String about;

}
