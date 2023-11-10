package dev.practice.blogproject.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "message")
    @NotBlank
    @Length(max = 500)
    private String message;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User sender;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User recipient;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}
