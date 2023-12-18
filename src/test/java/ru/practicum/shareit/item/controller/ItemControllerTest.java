package ru.practicum.shareit.item.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.request.service.ItemRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemAllFieldsDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.exceptions.NotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.http.MediaType.*;
import static java.nio.charset.StandardCharsets.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static java.time.LocalDateTime.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static java.util.List.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    private final String headerSharerUserId = "X-Sharer-User-Id";
    @MockBean
    ItemRequestService itemRequestService;
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final CommentDto commentDto = CommentDto.builder()
            .id(1)
            .text("qwerty")
            .itemId(1)
            .authorName("Paul")
            .created(now())
            .build();

    private final ItemAllFieldsDto itemExtendedDto = new ItemAllFieldsDto(
            1,
            "blue pen",
            "my blue pen",
            true,
            1,
            null,
            null,
            null,
            of(commentDto));

    private final ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("pen")
            .description("blue pen")
            .available(true)
            .ownerId(1)
            .requestId(1)
            .build();

    @Test
    void saveTest() throws Exception {
        when(itemService.save(any(), any(), anyInt()))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void saveItemRequestDtoIsNullTest() throws Exception {
        var itemDto2 = ItemDto.builder()
                .id(1)
                .name("pen")
                .description("blue pen")
                .available(true)
                .ownerId(1)
                .requestId(null)
                .build();

        when(itemService.save(any(), any(), anyInt()))
                .thenReturn(itemDto2);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto2))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$.id", is(itemDto2.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto2.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.get(any(), anyInt()))
                .thenReturn(itemExtendedDto);
        mvc.perform(get("/items/{itemId}", 1)
                        .header(headerSharerUserId, 1)
                )
                .andExpect(jsonPath("$.description", is(itemExtendedDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemExtendedDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemExtendedDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void updateTest() throws Exception {
        when(itemService.update(any(), anyInt()))
                .thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                )
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void saveCommentTest() throws Exception {
        when(itemService.saveComment(any(CommentDto.class), anyInt(), anyInt()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .header(headerSharerUserId, 1)
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(status().isOk());
    }

    @Test
    void searchTest() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(of(itemDto));
        mvc.perform(get("/items/search")
                        .header(headerSharerUserId, 1)
                        .param("size", "1")
                        .param("from", "0")
                        .param("text", "")
                )
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(status().isOk());
    }

    @Test
    void searchItemsByTextIsBlank() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void saveValidationExceptionTest() throws Exception {
        when(itemService.save(any(), any(), anyInt()))
                .thenThrow(ValidationException.class);
        mvc.perform(post("/items")
                        .header(headerSharerUserId, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateNotFoundExceptionTest() throws Exception {
        when(itemService.update(any(), anyInt()))
                .thenThrow(NotFoundException.class);
        mvc.perform(patch("/items/{itemId}", 1)
                        .header(headerSharerUserId, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void getNotFoundExceptionTest() throws Exception {
        when(itemService.get(any(), anyInt()))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/items/{itemId}", 1)
                        .header(headerSharerUserId, 1)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void saveCommentValidationExceptionTest() throws Exception {
        when(itemService.saveComment(any(CommentDto.class), anyInt(), anyInt()))
                .thenThrow(ValidationException.class);
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header(headerSharerUserId, 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());
    }

    @Test
    void findById() throws Exception {
        ItemAllFieldsDto itemResponseDto = new ItemAllFieldsDto();
        when(itemService.get(anyInt(),anyInt()))
                .thenReturn(itemResponseDto);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName())))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponseDto.getAvailable())));

    }

    @Test
    void getAll() throws Exception {
        ItemDtoWithBooking itemAllFieldsDto1 = new ItemDtoWithBooking();
        ItemDtoWithBooking itemAllFieldsDto2 = new ItemDtoWithBooking();
        List<ItemDtoWithBooking> items = List.of(itemAllFieldsDto1, itemAllFieldsDto2);
        when(itemService.getAllItems(anyInt(),anyInt(),anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        CommentDto commentDto = new CommentDto(1, "text comment", 1,
                "test", LocalDateTime.now());
        when(itemService.saveComment(any(), anyInt(), anyInt()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}