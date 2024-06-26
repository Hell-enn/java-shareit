package ru.practicum.shareit.datajpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class RequestJpaRepositoryTest {
    @Autowired
    private RequestJpaRepository requestJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    public void testPostItemRequestOk() {
        User requester = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(null, "description", requester, created);
        Assertions.assertNull(itemRequest.getId());
        userJpaRepository.save(requester);
        ItemRequest addedItemRequest = requestJpaRepository.save(itemRequest);
        Assertions.assertEquals(itemRequest.getId(), addedItemRequest.getId());
        Assertions.assertEquals(itemRequest.getDescription(), addedItemRequest.getDescription());
        Assertions.assertEquals(itemRequest.getRequester(), addedItemRequest.getRequester());
        Assertions.assertEquals(itemRequest.getCreated(), addedItemRequest.getCreated());
    }


    @Test
    public void testFindAllInPage() {

        User requester = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        LocalDateTime created = LocalDateTime.now();

        ItemRequest itemRequest1 = new ItemRequest(null, "description1", requester, created);
        ItemRequest itemRequest2 = new ItemRequest(null, "description2", requester, created.plusMinutes(1));
        ItemRequest itemRequest3 = new ItemRequest(null, "description3", requester, created.plusMinutes(2));
        ItemRequest itemRequest4 = new ItemRequest(null, "description4", requester, created.plusMinutes(3));
        ItemRequest itemRequest5 = new ItemRequest(null, "description5", requester, created.plusMinutes(4));

        Assertions.assertNull(requester.getId());
        User addedRequester = userJpaRepository.save(requester);
        Assertions.assertEquals(addedRequester.getId(), requester.getId());
        requestJpaRepository.save(itemRequest1);
        requestJpaRepository.save(itemRequest2);
        requestJpaRepository.save(itemRequest3);
        requestJpaRepository.save(itemRequest4);
        requestJpaRepository.save(itemRequest5);

        Pageable page1 = PageRequest.of(0, 5);

        List<ItemRequest> itemRequestList = List.of(itemRequest1, itemRequest2, itemRequest3, itemRequest4, itemRequest5);
        List<ItemRequest> addedItemRequestList1 = requestJpaRepository.findAllInPage(2L, page1);
        Assertions.assertEquals(itemRequestList, addedItemRequestList1);

        Pageable page2 = PageRequest.of(1, 5);
        List<ItemRequest> addedItemRequestList2 = requestJpaRepository.findAllInPage(2L, page2);
        Assertions.assertEquals(0, addedItemRequestList2.size());
    }


    @Test
    public void testFindByUserId() {

        User requester = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        LocalDateTime created = LocalDateTime.now();

        ItemRequest itemRequest1 = new ItemRequest(null, "description1", requester, created);
        ItemRequest itemRequest2 = new ItemRequest(null, "description2", requester, created.plusMinutes(1));
        ItemRequest itemRequest3 = new ItemRequest(null, "description3", requester, created.plusMinutes(2));
        ItemRequest itemRequest4 = new ItemRequest(null, "description4", requester, created.plusMinutes(3));
        ItemRequest itemRequest5 = new ItemRequest(null, "description5", requester, created.plusMinutes(4));

        User addedRequester = userJpaRepository.save(requester);
        Assertions.assertEquals(addedRequester.getId(), requester.getId());
        requestJpaRepository.save(itemRequest1);
        requestJpaRepository.save(itemRequest2);
        requestJpaRepository.save(itemRequest3);
        requestJpaRepository.save(itemRequest4);
        requestJpaRepository.save(itemRequest5);

        List<ItemRequest> itemRequestList = List.of(itemRequest1, itemRequest2, itemRequest3, itemRequest4, itemRequest5);
        List<ItemRequest> addedItemRequestList1 = requestJpaRepository.findByUserId(addedRequester.getId());
        Assertions.assertEquals(itemRequestList, addedItemRequestList1);
    }


    @Test
    public void testFindAmountOfRequests() {
        User requester = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        LocalDateTime created = LocalDateTime.now();

        ItemRequest itemRequest1 = new ItemRequest(null, "description1", requester, created);
        ItemRequest itemRequest2 = new ItemRequest(null, "description2", requester, created.plusMinutes(1));
        ItemRequest itemRequest3 = new ItemRequest(null, "description3", requester, created.plusMinutes(2));
        ItemRequest itemRequest4 = new ItemRequest(null, "description4", requester, created.plusMinutes(3));
        ItemRequest itemRequest5 = new ItemRequest(null, "description5", requester, created.plusMinutes(4));

        User addedRequester = userJpaRepository.save(requester);
        Assertions.assertEquals(addedRequester.getId(), requester.getId());
        requestJpaRepository.save(itemRequest1);
        requestJpaRepository.save(itemRequest2);
        requestJpaRepository.save(itemRequest3);
        requestJpaRepository.save(itemRequest4);
        requestJpaRepository.save(itemRequest5);

        int amount = 5;
        int requestsAmount = requestJpaRepository.findAmountOfRequests(addedRequester.getId());
        Assertions.assertEquals(amount, requestsAmount);
    }
}