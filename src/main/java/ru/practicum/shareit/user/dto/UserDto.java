package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UserDto {
    private final Integer id;
    private final String name;
    private final String email;
}
