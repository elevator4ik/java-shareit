package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Integer id;
    @Column(name = "create_date", nullable = false)
    private LocalDateTime created;
    @Column(name = "comment_text", nullable = false)
    private String text;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @Column(name = "item_id", nullable = false)
    private Integer item_id;
}
