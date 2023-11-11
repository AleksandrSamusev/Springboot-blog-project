package dev.practice.blogproject.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User recipient;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

}
