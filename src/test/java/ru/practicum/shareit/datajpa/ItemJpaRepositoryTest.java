package ru.practicum.shareit.datajpa;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemPagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemJpaRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemPagingAndSortingRepository itemPagingAndSortingRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private RequestJpaRepository requestJpaRepository;

    @Test
    public void testFindByUserId() {
        User owner = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User anotherOwner = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        Item item1 = new Item(null, "name1", "description1", true, owner, null);
        Item item2 = new Item(null, "name2", "description2", true, owner, null);
        Item item3 = new Item(null, "name3", "description3", true, owner, null);
        Item item4 = new Item(null, "name4", "description4", true, owner, null);
        Item item5 = new Item(null, "name5", "description5", true, anotherOwner, null);
        Item item6 = new Item(null, "name6", "description6", true, anotherOwner, null);

        userJpaRepository.save(owner);
        userJpaRepository.save(anotherOwner);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        Pageable page1 = PageRequest.of(0, 5);

        List<Item> ownerItems = List.of(item1, item2, item3, item4);
        List<Item> addedOwnerItems = itemPagingAndSortingRepository.findByUserId(owner.getId(), page1);
        Assertions.assertEquals(ownerItems, addedOwnerItems);

        Pageable page2 = PageRequest.of(1, 5);
        List<Item> emptyItems = itemPagingAndSortingRepository.findByUserId(owner.getId(), page2);
        Assertions.assertEquals(0, emptyItems.size());
    }


    @Test
    public void testFindByRequestId() {

        User owner = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User requester1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User requester2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User requester3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User requester4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        ItemRequest itemRequest1 = new ItemRequest(null, "description1", requester1, LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest(null, "description2", requester2, LocalDateTime.now());
        ItemRequest itemRequest3 = new ItemRequest(null, "description3", requester3, LocalDateTime.now());
        ItemRequest itemRequest4 = new ItemRequest(null, "description4", requester4, LocalDateTime.now());

        Item item1 = new Item(null, "name1", "description1", true, owner, itemRequest1);
        Item item2 = new Item(null, "name2", "description2", true, owner, itemRequest2);
        Item item3 = new Item(null, "name3", "description3", true, owner, itemRequest2);
        Item item4 = new Item(null, "name4", "description4", true, owner, itemRequest2);
        Item item5 = new Item(null, "name5", "description5", true, owner, itemRequest3);
        Item item6 = new Item(null, "name6", "description6", true, owner, itemRequest4);

        userJpaRepository.save(owner);
        userJpaRepository.save(requester1);
        userJpaRepository.save(requester2);
        userJpaRepository.save(requester3);
        userJpaRepository.save(requester4);

        requestJpaRepository.save(itemRequest1);
        requestJpaRepository.save(itemRequest2);
        requestJpaRepository.save(itemRequest3);
        requestJpaRepository.save(itemRequest4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        List<Item> items = List.of(item2, item3, item4);
        List<Item> addedItems = itemPagingAndSortingRepository.findByRequestId(itemRequest2.getId());
        Assertions.assertEquals(items, addedItems);

        items = List.of(item1);
        addedItems = itemPagingAndSortingRepository.findByRequestId(itemRequest1.getId());
        Assertions.assertEquals(items, addedItems);

        items = List.of();
        addedItems = itemPagingAndSortingRepository.findByRequestId(10L);
        Assertions.assertEquals(items, addedItems);
    }


    @Test
    public void testFindAllBySubstring() {

        User owner = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User requester1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User requester2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User requester3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User requester4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        ItemRequest itemRequest1 = new ItemRequest(null, "description1", requester1, LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest(null, "description2", requester2, LocalDateTime.now());
        ItemRequest itemRequest3 = new ItemRequest(null, "description3", requester3, LocalDateTime.now());
        ItemRequest itemRequest4 = new ItemRequest(null, "description4", requester4, LocalDateTime.now());

        Item item1 = new Item(null, "name1", "description1", true, owner, itemRequest1);
        Item item2 = new Item(null, "name2", "description2", true, owner, itemRequest2);
        Item item3 = new Item(null, "name3", "description3", true, owner, itemRequest2);
        Item item4 = new Item(null, "name4", "description4", true, owner, itemRequest2);
        Item item5 = new Item(null, "name5", "description5", true, owner, itemRequest3);
        Item item6 = new Item(null, "name6", "description6", true, owner, itemRequest4);

        userJpaRepository.save(owner);
        userJpaRepository.save(requester1);
        userJpaRepository.save(requester2);
        userJpaRepository.save(requester3);
        userJpaRepository.save(requester4);

        requestJpaRepository.save(itemRequest1);
        requestJpaRepository.save(itemRequest2);
        requestJpaRepository.save(itemRequest3);
        requestJpaRepository.save(itemRequest4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        Pageable page1 = PageRequest.of(0, 6);

        List<Item> items = List.of(item1, item2, item3, item4, item5, item6);
        List<Item> addedItems = itemPagingAndSortingRepository.findAllBySubstring("descr", "descr", page1);
        Assertions.assertEquals(items, addedItems);

        items = List.of();
        addedItems = itemPagingAndSortingRepository.findAllBySubstring("qwerty", "yuiop", page1);
        Assertions.assertEquals(items, addedItems);
    }


    @Test
    public void testFindItemsAmountByOwnerId() {
        User owner = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User anotherOwner = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        Item item1 = new Item(null, "name1", "description1", true, owner, null);
        Item item2 = new Item(null, "name2", "description2", true, owner, null);
        Item item3 = new Item(null, "name3", "description3", true, owner, null);
        Item item4 = new Item(null, "name4", "description4", true, owner, null);
        Item item5 = new Item(null, "name5", "description5", true, anotherOwner, null);
        Item item6 = new Item(null, "name6", "description6", true, anotherOwner, null);

        userJpaRepository.save(owner);
        userJpaRepository.save(anotherOwner);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        int ownerItems = 4;
        int addedOwnerItems = itemPagingAndSortingRepository.findItemsAmountByOwnerId(owner.getId());
        Assertions.assertEquals(ownerItems, addedOwnerItems);
    }


    @Test
    public void testFindAmountBySubstring() {
        User owner = new User(null, "Andrey Andreev", "andreyandreev@gmail.com");
        User requester1 = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        User requester2 = new User(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        User requester3 = new User(null, "Sergey Sergeev", "sergeysergeev@gmail.com");
        User requester4 = new User(null, "Viktor Viktorov", "viktorviktorov@gmail.com");

        ItemRequest itemRequest1 = new ItemRequest(null, "description1", requester1, LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest(null, "description2", requester2, LocalDateTime.now());
        ItemRequest itemRequest3 = new ItemRequest(null, "description3", requester3, LocalDateTime.now());
        ItemRequest itemRequest4 = new ItemRequest(null, "description4", requester4, LocalDateTime.now());

        Item item1 = new Item(null, "name1", "description1", true, owner, itemRequest1);
        Item item2 = new Item(null, "name2", "description2", true, owner, itemRequest2);
        Item item3 = new Item(null, "name3", "description3", true, owner, itemRequest2);
        Item item4 = new Item(null, "name4", "description4", true, owner, itemRequest2);
        Item item5 = new Item(null, "name5", "description5", true, owner, itemRequest3);
        Item item6 = new Item(null, "name6", "description6", true, owner, itemRequest4);

        userJpaRepository.save(owner);
        userJpaRepository.save(requester1);
        userJpaRepository.save(requester2);
        userJpaRepository.save(requester3);
        userJpaRepository.save(requester4);

        requestJpaRepository.save(itemRequest1);
        requestJpaRepository.save(itemRequest2);
        requestJpaRepository.save(itemRequest3);
        requestJpaRepository.save(itemRequest4);

        itemPagingAndSortingRepository.save(item1);
        itemPagingAndSortingRepository.save(item2);
        itemPagingAndSortingRepository.save(item3);
        itemPagingAndSortingRepository.save(item4);
        itemPagingAndSortingRepository.save(item5);
        itemPagingAndSortingRepository.save(item6);

        int items = 6;
        int addedItems = itemPagingAndSortingRepository.findAmountBySubstring("descr", "descr");
        Assertions.assertEquals(items, addedItems);

        items = 0;
        addedItems = itemPagingAndSortingRepository.findAmountBySubstring("qwerty", "yuiop");
        Assertions.assertEquals(items, addedItems);
    }
}