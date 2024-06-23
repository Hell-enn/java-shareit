package ru.practicum.shareit.datajpa;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemPagingAndSortingRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentJpaRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentJpaRepository commentJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ItemPagingAndSortingRepository itemJpaRepository;

    @Test
    public void testFindByItemId() {

        User user1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User user2 = new User(null, "Ivan Ivanov", "ivanivanov@gmail.com");
        User user3 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User user4 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");

        User owner = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        Item item = new Item(null, "name", "description", true, owner, null);

        Comment comment1 = new Comment(null, "text1", user1, item, LocalDateTime.now());
        Comment comment2 = new Comment(null, "text2", user2, item, LocalDateTime.now());
        Comment comment3 = new Comment(null, "text3", user3, item, LocalDateTime.now());
        Comment comment4 = new Comment(null, "text4", user4, item, LocalDateTime.now());

        userJpaRepository.save(owner);
        userJpaRepository.save(user1);
        userJpaRepository.save(user2);
        userJpaRepository.save(user3);
        userJpaRepository.save(user4);
        itemJpaRepository.save(item);
        commentJpaRepository.save(comment1);
        commentJpaRepository.save(comment2);
        commentJpaRepository.save(comment3);
        commentJpaRepository.save(comment4);

        List<Comment> commentList = List.of(comment1, comment2, comment3, comment4);
        List<Comment> commentsByItemId = commentJpaRepository.findByItemId(item.getId());

        Assertions.assertEquals(commentList, commentsByItemId);
    }


    @Test
    public void testFindCommentByItemIdAndBookerId() {

        User user1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User user2 = new User(null, "Ivan Ivanov", "ivanivanov@gmail.com");
        User user3 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User user4 = new User(null, "Ilya Ilev", "ilyailev@gmail.com");

        User owner = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        Item item = new Item(null, "name", "description", true, owner, null);

        Comment comment1 = new Comment(null, "text1", user1, item, LocalDateTime.now());
        Comment comment2 = new Comment(null, "text2", user2, item, LocalDateTime.now());
        Comment comment3 = new Comment(null, "text3", user3, item, LocalDateTime.now());
        Comment comment4 = new Comment(null, "text4", user4, item, LocalDateTime.now());
        Comment comment5 = new Comment(null, "text5", user4, item, LocalDateTime.now());

        userJpaRepository.save(owner);
        userJpaRepository.save(user1);
        userJpaRepository.save(user2);
        userJpaRepository.save(user3);
        userJpaRepository.save(user4);
        itemJpaRepository.save(item);
        commentJpaRepository.save(comment1);
        commentJpaRepository.save(comment2);
        commentJpaRepository.save(comment3);
        commentJpaRepository.save(comment4);
        commentJpaRepository.save(comment5);

        List<Comment> commentList = List.of(comment4, comment5);
        List<Comment> commentsByItemId = commentJpaRepository.findCommentByItemIdAndBookerId(item.getId(), user4.getId());

        Assertions.assertEquals(commentList, commentsByItemId);
    }
}
