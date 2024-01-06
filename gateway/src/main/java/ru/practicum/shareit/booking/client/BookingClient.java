package ru.practicum.shareit.booking.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getBookings(Integer userId,
                                              BookingState state,
                                              Integer from,
                                              Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBooking(Integer userId,
                                             Integer bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> save(BookingSavingDto bookingSavingDto, Integer userId) {
        return post("", userId, bookingSavingDto);
    }


    public ResponseEntity<Object> approveBooking(Integer userId, Integer bookingId, boolean isApproved) {
        return patch("/" + bookingId + "?approved=" + isApproved, userId);
    }

    public ResponseEntity<Object> getByOwner(Integer ownerId, BookingState state, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", ownerId, parameters);
    }
}