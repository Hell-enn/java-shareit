package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RequestJpaRepository extends PagingAndSortingRepository<ItemRequest, Long>, CrudRepository<ItemRequest, Long> {
    @Query("select r from ItemRequest as r join r.requester as u where u.id <> ?1")
    List<ItemRequest> findAllInPage(Long userId, Pageable pageable);

    @Query("select r from ItemRequest as r join r.requester as u where u.id = ?1")
    List<ItemRequest> findByUserId(Long userId);

    @Query(value = "select count(*) from requests as r where r.user_id = ?1", nativeQuery = true)
    int findAmountOfRequests(Long userId);
}
