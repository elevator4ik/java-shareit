package ru.practicum.shareit.item.model;

import lombok.*;
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
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "author_id")
    private User author;
    @ManyToOne(targetEntity = Item.class)
    @JoinColumn(name = "item_id")
    private Item item;
}
