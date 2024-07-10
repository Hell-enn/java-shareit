package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestInDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestJpaRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestJpaRepository requestJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ItemRequestMapper itemRequestMapper;


    @Override
    public ItemRequestOutDto postItemRequest(Long userId, ItemRequestInDto itemRequestDto) {
        if (!userJpaRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных", userId);
            throw new NotFoundException("Пользователь не существует!");
        }
        log.debug("Сохранение запроса вещи \"{}\" в базу данных от пользователя с id={}",
                itemRequestDto.getDescription(), userId);
        return itemRequestMapper.toItemRequestOutDto(requestJpaRepository.save(itemRequestMapper.toItemRequest(itemRequestDto, userId)));
    }


    @Override
    public ItemRequestOutDto patchItemRequest(Long userId, ItemRequestInDto itemRequestDto, Long requestId) {
        ItemRequest itemRequest = validateUpdateItemRequest(requestId, userId);
        itemRequestMapper.updateItemRequest(itemRequestDto, itemRequest);
        log.debug("Запрос вещи c id={} обновлен пользователем {}!", requestId, userId);
        return itemRequestMapper.toItemRequestOutDto(requestJpaRepository.save(itemRequest));
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestOutDto> getItemRequests(Long userId) {
        if (!userJpaRepository.existsById(userId)) {
            log.debug("Пользователь с id={} не существует", userId);
            throw new NotFoundException("Пользователь не существует!");
        }
        List<ItemRequestOutDto> itemRequestDtoList = itemRequestMapper.toItemRequestOutDtos(requestJpaRepository.findByUserId(userId));
        log.debug("Возвращаем список запросов вещей пользователя с id={} в количестве {}", userId, itemRequestDtoList.size());
        return itemRequestDtoList;
    }


    @Override
    @Transactional(readOnly = true)
    public ItemRequestOutDto getItemRequest(Long id, Long userId) {
        if (!userJpaRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных", userId);
            throw new NotFoundException("Пользователь не существует!");
        }
        ItemRequest itemRequest = requestJpaRepository.findById(id).orElseThrow(() -> new NotFoundException("Запрос не найден!"));
        log.debug("Возвращаем объект запроса вещи с id={}", id);
        return itemRequestMapper.toItemRequestOutDto(itemRequest);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestOutDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        if (!userJpaRepository.existsById(userId)) {
            log.debug("Объект типа User с id={} отсутствует в базе данных!", userId);
            throw new NotFoundException("Пользователь не найден!");
        }

        int amountOfRequests = requestJpaRepository.findAmountOfRequests(userId);
        int pageNum = amountOfRequests > from ? from / size : 0;

        Pageable page = PageRequest
                .of(pageNum, size, Sort.by("created").descending())
                .toOptional()
                .orElseThrow(() -> new RuntimeException("Ошибка преобразования страницы!"));
        List<ItemRequestOutDto> requestDtos = itemRequestMapper.toItemRequestOutDtos(requestJpaRepository.findAllInPage(userId, page));
        log.debug("Возвращаем список запросов вещей пользователя с id={} в количестве {}", userId, requestDtos.size());
        return requestDtos;
    }


    /**
     * Закрытый служебный метод проверяет объект типа ItemRequest
     * на соответствие ряду условий. Используется впоследствие
     * для валидации объекта типа ItemRequest при попытке его обновления
     * в репозитории.
     * В случае неудачи выбрасывает исключение с сообщением об ошибке.
     *
     * @param requestId (идентификатор запроса к вещи)
     * @param userId (идентификатор пользователя, отправившего запрос на редактирование)
     *
     * @return ItemRequest
     */
    private ItemRequest validateUpdateItemRequest(Long requestId, Long userId) {
        ItemRequest itemRequest = requestJpaRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден!"));
        User user = itemRequest.getRequester();
        if (user != null && !user.getId().equals(userId)) {
            log.debug("Пользователь с id={} не может редактировать запрос, поскольку не является автором этого запроса", userId);
            throw new BadRequestException("Пользователь не может редактировать запрос!");
        }

        return itemRequest;
    }
}