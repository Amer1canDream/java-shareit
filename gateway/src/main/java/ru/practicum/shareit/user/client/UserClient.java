package ru.practicum.shareit.user.client;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> updateUser(UserDto userDto, Integer userId) {
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("", userDto);
    }

    public ResponseEntity<Object> getUser(Integer userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> getUsers() {
        return get("");
    }

    public void deleteUser(Integer userId) {
        delete("/" + userId);
    }
}