package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    @Query("select it from Item as it join it.owner as u where u.id = ?1 ")
    List<Item> findByUserId(Long userId);

    List<Item> findAllByNameContainingIgnoreCaseAndAvailableTrue(String name);

    List<Item> findAllByDescriptionContainingIgnoreCaseAndAvailableTrue(String email);
}