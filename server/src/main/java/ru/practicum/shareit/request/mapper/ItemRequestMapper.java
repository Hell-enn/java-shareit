package ru.practicum.shareit.request.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemPagingAndSortingRepository;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;

@AllArgsConstructor
@Component
public class ItemRequestMapper {

    private final UserJpaRepository userJpaRepository;
    private final ItemPagingAndSortingRepository itemPagingAndSortingRepository;
    private final ItemMapper itemMapper;

    public ItemRequestOutDto toItemRequestOutDto(ItemRequest itemRequest) {
        List<ItemResponseDto> responseItemDtos = new ArrayList<>();
        itemPagingAndSortingRepository.findByRequestId(itemRequest.getId()).forEach(item -> responseItemDtos.add(itemMapper.toItemResponseDto(item)));
        return new ItemRequestOutDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester() != null ? itemRequest.getRequester().getId() : null,
                itemRequest.getCreated(),
                responseItemDtos
        );
    }


    public ItemRequest toItemRequest(ItemRequestInDto itemRequestDto, Long userId) {

        User user = userJpaRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден!"));
        return new ItemRequest(
                0L,
                itemRequestDto.getDescription(),
                user,
                LocalDateTime.now()
        );
    }


    public void updateItemRequest(ItemRequestInDto itemRequestInDto, ItemRequest itemRequest) {
        if (itemRequestInDto.getDescription() != null && !itemRequestInDto.getDescription().equals(itemRequest.getDescription()))
            itemRequest.setDescription(itemRequestInDto.getDescription());
    }


    public List<ItemRequestOutDto> toItemRequestOutDtos(List<ItemRequest> itemRequests) {
        List<ItemRequestOutDto> itemRequestOutDtoList = new ArrayList<>();
        itemRequests.forEach(itemRequest -> itemRequestOutDtoList.add(toItemRequestOutDto(itemRequest)));
        return itemRequestOutDtoList;
    }
}
