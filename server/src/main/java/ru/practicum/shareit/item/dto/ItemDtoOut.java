package ru.practicum.shareit.item.dto;

import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDtoOut extends ItemDtoOutAbs {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private Integer requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDtoOut that = (ItemDtoOut) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(available, that.available) &&
                Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, requestId);
    }
}
