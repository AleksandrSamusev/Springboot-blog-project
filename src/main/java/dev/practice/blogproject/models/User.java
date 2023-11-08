package dev.practice.blogproject.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name")
    @Length(max = 50)
    @NotNull
    private String firstName;

    @Column(name = "last_name")
    @Length(max = 50)
    @NotNull
    private String lastName;

    @Column(name = "username")
    @Length(max = 50)
    @NotNull
    private String username;

    @Column(name = "email")
    @Length(max = 50)
    @Email
    @NotNull
    private String email;

    @Column(name = "birthdate")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull
    @Past
    private LocalDate birthdate;

    @Column(name = "about")
    @NotNull
    @Length(max = 1000)
    private String about;

    @Column(name = "is_banned")
    @NotNull
    private Boolean isBanned = false;

    @OneToMany(mappedBy = "sender")
    private Set<Message> sentMessages;

    @OneToMany(mappedBy = "recipient")
    private Set<Message> receivedMessages;
}
