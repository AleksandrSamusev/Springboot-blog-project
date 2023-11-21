package dev.practice.mainApp.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "birthdate", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "about")
    private String about;

    @Column(name = "is_banned", nullable = false)
    private Boolean isBanned = false;

    @OneToMany(mappedBy = "sender")
    private Set<Message> sentMessages;

    @OneToMany(mappedBy = "recipient")
    private Set<Message> receivedMessages;

    @OneToMany(mappedBy = "author")
    private Set<Article> articles;

    @OneToMany(mappedBy = "commentAuthor")
    private Set<Comment> comments;

}
