package ru.practicum.shareit.integrational;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemGetDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final BookingService bookingService;

    @Test
    public void testGetItemsBySearch() {

        UserDto ownerDto1 = new UserDto(null, "Ivan Ivanov", "ivanivanov@gmail.com");
        UserDto ownerDto2 = new UserDto(null, "Petr Petrov", "petrpetrov@gmail.com");
        UserDto ownerDto3 = new UserDto(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto ownerDto4 = new UserDto(null, "Andrey Andreev", "andreyandreev@gmail.com");

        ItemDto itemDto1 = new ItemDto(null, "name1", "description1", true, 1L, null, List.of());
        ItemDto itemDto2 = new ItemDto(null, "name2", "description2", true, 2L, null, List.of());
        ItemDto itemDto3 = new ItemDto(null, "name3", "description3", true, 3L, null, List.of());

        UserDto addedUser1 = userService.postUser(ownerDto1);
        UserDto addedUser2 = userService.postUser(ownerDto2);
        UserDto addedUser3 = userService.postUser(ownerDto3);
        UserDto addedUser4 = userService.postUser(ownerDto4);

        ItemDto addedItem1 = itemService.postItem(addedUser1.getId(), itemDto1);
        ItemDto addedItem2 = itemService.postItem(addedUser2.getId(), itemDto2);
        ItemDto addedItem3 = itemService.postItem(addedUser3.getId(), itemDto3);

        ItemGetDto itemGetDto1 = new ItemGetDto(
                null, "name1", "description1", true, 1L, null, null, null, List.of());
        ItemGetDto itemGetDto2 = new ItemGetDto(
                null, "name2", "description2", true, 2L, null, null, null, List.of());
        ItemGetDto itemGetDto3 = new ItemGetDto(
                null, "name3", "description3", true, 3L, null, null, null, List.of());

        itemGetDto1.setId(addedItem1.getId());
        itemGetDto1.setBooker(addedUser1.getId());
        itemGetDto2.setId(addedItem2.getId());
        itemGetDto2.setBooker(addedUser2.getId());
        itemGetDto3.setId(addedItem3.getId());
        itemGetDto3.setBooker(addedUser3.getId());

        TypedQuery<Item> query = em.createQuery("SELECT it " +
                "FROM Item as it " +
                "WHERE (lower(it.name) like lower(concat('%',:text1,'%')) OR lower(it.description) like lower(concat('%',:text2,'%'))) " +
                "AND it.available = true", Item.class);

        List<ItemGetDto> itemGetDtoList = itemMapper.toItemGetDtos(query.setParameter("text1", "desc").setParameter("text2", "desc").getResultList(), addedUser4.getId());

        assertThat(itemGetDtoList, notNullValue());
        assertThat(itemGetDtoList.size(), equalTo(List.of(itemGetDto1, itemGetDto2, itemGetDto3).size()));
        assertThat(itemGetDtoList, equalTo(List.of(itemGetDto1, itemGetDto2, itemGetDto3)));

        ItemDto itemDto11 = new ItemDto(null, "name11", "description11", true, 1L, null, List.of());
        Item item11 = new Item(null, "name1", "description1", true, null, null);
        itemMapper.updateItemFromDto(itemDto11, item11);
    }


    @Test
    public void testAddComment() {
        UserDto authorDto = new UserDto(null, "Petr Petrov", "petrpetrov@gmail.com");
        UserDto ownerDto = new UserDto(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");

        UserDto addedAuthorDto = userService.postUser(authorDto);
        UserDto addedOwnerDto = userService.postUser(ownerDto);
        ItemDto itemDto = new ItemDto(
                null, "name1", "description1", true, addedOwnerDto.getId(), null, List.of("comment1", "comment2"));
        ItemDto addedItemDto = itemService.postItem(addedOwnerDto.getId(), itemDto);
        CommentDto commentDto1 = new CommentDto(
                1L, "comment1", addedAuthorDto.getId(), addedItemDto.getId(), addedAuthorDto.getName(), LocalDateTime.now());

        Comment comment = commentMapper.toComment(commentDto1, addedAuthorDto.getId(), addedItemDto.getId());
        comment.setId(commentDto1.getId());
        CommentDto mappedCommentDto = commentMapper.toCommentDto(comment);

        assertThat(mappedCommentDto, notNullValue());
        assertThat(mappedCommentDto.getText(), equalTo(commentDto1.getText()));
        assertThat(mappedCommentDto.getAuthor(), equalTo(commentDto1.getAuthor()));
        assertThat(mappedCommentDto.getItem(), equalTo(commentDto1.getItem()));
        assertThat(mappedCommentDto.getAuthorName(), equalTo(commentDto1.getAuthorName()));
    }


    @Test
    public void testItemMapping() {
        User owner = new User(1L, "Petr Petrov", "petrpetrov@gmail.com");
        User requester = new User(2L, "Ivan Ivanov", "ivanivanov@gmail.com");
        LocalDateTime now = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(1L, "description", requester, now);
        Item item = new Item(1L, "name", "description", true, owner, itemRequest);

        ItemResponseDto itemResponseDto = new ItemResponseDto(1L, "name", "description", 1L, true);
        Assertions.assertEquals(itemResponseDto, itemMapper.toItemResponseDto(item));
    }
}