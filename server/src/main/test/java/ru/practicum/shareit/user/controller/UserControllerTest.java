package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService mockUserService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDtoCreateTest;

    private UserDto userDtoCreated;

    private UserDto userDtoUpdateTest;

    private UserDto userDtoUpdated;

    @BeforeEach
    void setUp() {
        userDtoCreateTest = UserDto.builder()
                .name("userCreate")
                .email("userTest@email.com")
                .build();
        userDtoCreated = UserDto.builder()
                .id(1)
                .name("userCreate")
                .email("userTest@email.com")
                .build();
        userDtoUpdateTest = UserDto.builder()
                .name("userUpdate")
                .build();
        userDtoUpdated = UserDto.builder()
                .id(1)
                .name("userUpdate")
                .email("userTest@email.com")
                .build();
    }

    @AfterEach
    void tearDown() {
        userDtoCreateTest = null;
        userDtoCreated = null;
        userDtoUpdateTest = null;
        userDtoUpdated = null;
    }

    @Test
    void createTest() throws Exception {
        when(mockUserService.save(userDtoCreateTest))
                .thenReturn(userDtoCreated);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoCreateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoCreated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDtoCreated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoCreated.getEmail())));
    }

    @Test
    void updateTest() throws Exception {
        when(mockUserService.update(userDtoUpdateTest, 1))
                .thenReturn(userDtoUpdated);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdateTest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdated.getEmail())));
    }

    @Test
    void getUserDtoTest() throws Exception {
        when(mockUserService.get(1))
                .thenReturn(userDtoUpdated);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(userDtoUpdated.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdated.getEmail())));
    }

    @Test
    void deleteUserDtoTest() throws Exception {
        mvc.perform(delete("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(mockUserService).delete(1);
    }

    @Test
    void getAllTest() throws Exception {
        when(mockUserService.getUsers())
                .thenReturn(List.of(userDtoUpdated));

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(userDtoUpdated.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(userDtoUpdated.getName())))
                .andExpect(jsonPath("$[0].email", is(userDtoUpdated.getEmail())));
    }

    @Test
    void deleteByIdTest() throws Exception {
        doNothing().when(mockUserService).delete(anyInt());

        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(mockUserService, times(1)).delete(anyInt());
    }

    @Test
    void getByIdTest() throws Exception {
        when(mockUserService.getUsers())
                .thenReturn(List.of(userDtoUpdated));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDtoUpdated))));
    }
}