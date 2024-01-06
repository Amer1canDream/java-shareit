package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    Page<ItemRequest> findItemRequestByRequester_IdIsNotOrderByCreatedDesc(Integer userId, Pageable pageable);

    List<ItemRequest> findItemRequestByRequester_IdIsNotOrderByCreatedDesc(Integer userId);

    List<ItemRequest> findItemRequestByRequesterOrderByCreatedDesc(User user);
}