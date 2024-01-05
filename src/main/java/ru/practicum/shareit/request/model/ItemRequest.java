package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

import java.time.LocalDateTime;

<<<<<<<< HEAD:server/src/main/java/ru/practicum/shareit/request/model/ItemRequest.java
/**
 * TODO Sprint add-item-requests.
 */
========
import static javax.persistence.GenerationType.IDENTITY;

>>>>>>>> main:src/main/java/ru/practicum/shareit/request/model/ItemRequest.java
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
