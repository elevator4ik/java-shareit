package ru.practicum.shareit.request.model;


import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Entity
@Table(name = "requests", schema = "public")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false)
    private Integer id;
    @Column(name = "description", nullable = false)
    private String description;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requestOwner;
    @OneToMany(targetEntity = Item.class)
    @JoinColumn(name = "request_id")
    private List<Item> requestedItems;
    @Column(name = "create_date", nullable = false)
    private LocalDateTime created;
}
