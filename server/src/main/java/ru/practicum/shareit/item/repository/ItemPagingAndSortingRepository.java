package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemPagingAndSortingRepository extends PagingAndSortingRepository<Item, Long>, CrudRepository<Item, Long> {
    @Query("select it from Item as it join it.owner as u where u.id = ?1 order by it.id")
    List<Item> findByUserId(Long userId, Pageable page);

    @Query("select it from Item as it join it.request as r where r.id = ?1 ")
    List<Item> findByRequestId(Long requestId);

    @Query("SELECT it " +
            "FROM Item as it " +
            "WHERE (lower(it.name) like lower(concat('%',?1,'%')) OR lower(it.description) like lower(concat('%',?2,'%'))) " +
            "AND it.available = true")
    List<Item> findAllBySubstring(String name, String description, Pageable page);

    @Query(value = "select count(*) from items as i where i.user_id = ?1", nativeQuery = true)
    int findItemsAmountByOwnerId(Long ownerId);

    @Query("SELECT count(it) " +
            "FROM Item as it " +
            "WHERE (lower(it.name) like lower(concat('%',?1,'%')) OR lower(it.description) like lower(concat('%',?2,'%'))) " +
            "AND it.available = true")
    int findAmountBySubstring(String name, String description);
}