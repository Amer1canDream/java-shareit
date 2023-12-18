package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PaginationTest {

    @Test
    void paginationTest() {
        PageRequest pageRequest = Pagination.makePageRequest(1, 1, Sort.by("id"));
        assertThat(pageRequest.first().equals(0));
    }
}
