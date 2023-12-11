package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    String searchQuery = "SELECT item FROM Item item " +
            "WHERE item.available = TRUE " +
            "AND (UPPER(item.name) LIKE UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(item.description) LIKE UPPER(CONCAT('%', ?1, '%')))";

    Page<Item> findAllByOwner_IdIs(Integer ownerId, Pageable pageable);

    List<Item> findAllByRequestIn(List<ItemRequest> requests);

    @Query(searchQuery)
    Page<Item> search(String text, Pageable pageable);

    List<Item> findAllByRequest_IdIs(Integer requestId);

    List<Item> findAllByOwner_IdIs(Integer ownerId);

    @Query(searchQuery)
    List<Item> search(String text);

    @Query("select i from Item i where i.owner.id = :ownerId order by i.id")
    List<Item> findByOwner(@Param("ownerId") int ownerId);

    Collection<Item> findByOwnerIdOrderByIdAsc(Integer userId);
}
