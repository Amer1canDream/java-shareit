package ru.practicum.shareit.user.controller;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exceptions.NotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.exceptions.EmailException;
import ru.practicum.shareit.user.dto.UserDto;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.http.MediaType.*;
import static java.nio.charset.StandardCharsets.*;
import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final UserDto userDto = UserDto.builder()
            .id(1)
            .name("Michael")
            .email("michael@mail.com")
            .build();

    @Test
    void saveTest() throws Exception {
        when(userService.save(any(UserDto.class)))
                .thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void updateTest() throws Exception {
        when(userService.update(any(), anyInt()))
                .thenReturn(userDto);
        mvc.perform(patch("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.get(any()))
                .thenReturn(userDto);
        mvc.perform(get("/users/{userId}", 1)
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id", is(userDto.getId()), Integer.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        mvc.perform(delete("/users/{userId}", 1)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void saveUserEmailExceptionTest() throws Exception {
        when(userService.save(any(UserDto.class)))
                .thenThrow(EmailException.class);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .contentType(APPLICATION_JSON)
                        .characterEncoding(UTF_8)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isConflict());
    }

    @Test
    void getUserNotFoundExceptionTest() throws Exception {
        when(userService.get(any()))
                .thenThrow(NotFoundException.class);
        mvc.perform(get("/users/{userId}", 7)
                )
                .andExpect(status().isNotFound());
    }
}