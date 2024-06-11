package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment as c " +
            "join c.item as i " +
            "where i.id = ?1")
    List<Comment> findByItemId(Long itemId);

    @Query("select c from Comment as c " +
            "join c.author as u " +
            "join c.item as i " +
            "where i.id = ?1 and u.id = ?2")
    List<Comment> findCommentByItemIdAndBookerId(Long itemId, Long authorId);
}