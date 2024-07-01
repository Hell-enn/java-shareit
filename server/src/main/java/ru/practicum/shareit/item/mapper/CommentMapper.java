package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemPagingAndSortingRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@Component
public class CommentMapper {
    private final UserJpaRepository userJpaRepository;
    private final ItemPagingAndSortingRepository itemPagingAndSortingRepository;

    public CommentDto toCommentDto(Comment comment) {
        User user = comment.getAuthor();
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                user != null ? user.getId() : null,
                comment.getItem() != null ? comment.getItem().getId() : null,
                user != null ? user.getName() : null,
                comment.getCreated()
        );
    }

    public Comment toComment(CommentDto commentDto, Long userId, Long itemId) {
        Optional<User> userOpt = userJpaRepository.findById(userId);
        User user = null;
        if (userOpt.isPresent())
            user = userOpt.get();
        Optional<Item> itemOpt = itemPagingAndSortingRepository.findById(itemId);
        Item item = null;
        if (itemOpt.isPresent())
            item = itemOpt.get();
        return new Comment(0L, commentDto.getText(), user, item, LocalDateTime.now());
    }
}
