package ru.practicum.shareit.user.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;


public interface UserJpaRepository extends PagingAndSortingRepository<User, Long> {
    List<User> findAll();
}