package ru.practicum.shareit.integrational;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;

    @Test
    public void testGetAllItemRequestsOk() {

        UserDto requesterDto1 = new UserDto(null, "Ivan Ivanov", "ivanivanov@gmail.com");
        UserDto requesterDto2 = new UserDto(null, "Petr Petrov", "petrpetrov@gmail.com");
        UserDto requesterDto3 = new UserDto(null, "Alexey Alexeev", "alexeyalexeev@gmail.com");
        UserDto requesterDto4 = new UserDto(null, "Andrey Andreev", "andreyandreev@gmail.com");

        ItemRequestInDto itemRequestInDto1 = new ItemRequestInDto(null, "description1", null);
        ItemRequestInDto itemRequestInDto2 = new ItemRequestInDto(null, "description2", null);
        ItemRequestInDto itemRequestInDto3 = new ItemRequestInDto(null, "description3", null);

        UserDto addedRequester1 = userService.postUser(requesterDto1);
        UserDto addedRequester2 = userService.postUser(requesterDto2);
        UserDto addedRequester3 = userService.postUser(requesterDto3);
        UserDto addedRequester4 = userService.postUser(requesterDto4);

        itemRequestInDto1.setRequestor(addedRequester4.getId());
        itemRequestInDto2.setRequestor(addedRequester4.getId());
        itemRequestInDto3.setRequestor(addedRequester4.getId());

        ItemRequestOutDto itemRequestOutDto1 = itemRequestService.postItemRequest(addedRequester4.getId(), itemRequestInDto1);
        ItemRequestOutDto itemRequestOutDto2 = itemRequestService.postItemRequest(addedRequester4.getId(), itemRequestInDto2);
        ItemRequestOutDto itemRequestOutDto3 = itemRequestService.postItemRequest(addedRequester4.getId(), itemRequestInDto3);

        TypedQuery<ItemRequest> query = em.createQuery("Select ir " +
                "from ItemRequest ir " +
                "join ir.requester u " +
                "where u.id = :userId", ItemRequest.class);

        List<ItemRequestOutDto> itemRequestOutDtoList = itemRequestMapper.toItemRequestOutDtos(query.setParameter("userId", addedRequester4.getId()).getResultList());

        assertThat(itemRequestOutDtoList, notNullValue());
        assertThat(itemRequestOutDtoList.size(), equalTo(List.of(itemRequestOutDto1, itemRequestOutDto2, itemRequestOutDto3).size()));
        assertThat(itemRequestOutDtoList, equalTo(List.of(itemRequestOutDto1, itemRequestOutDto2, itemRequestOutDto3)));
    }
}