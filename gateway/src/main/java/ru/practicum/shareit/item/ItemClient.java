package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemPatchDto;
import ru.practicum.shareit.item.dto.ItemPostDto;

import java.util.Map;

/**
 * Класс ItemClient - клиентский слой микросервиса-шлюза приложения,
 * отвечающий за преобразование маршрутизированных из слоя-контроллера
 * объектов вещей и прочих необходимых объектов и идентификаторов
 * в HTTP-запросы к микросервису-серверу, где содержится основная бизнес-логика
 * приложения.
 * Наследует от базового класса BaseClient, где инкапсулирована логика формирования
 * всех HTTP-запросов к серверному микросервису приложения, и использует его методы
 * в основе собственных специализированных.
 * Поля:
 *  API_PREFIX - строковая константа, используемая при создании объекта типа RestTemplate
 *  в качестве префикса к адресу микросервиса-сервера, для формирования запросов,
 *  касающихся бронируемых вещей.
 */
@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    /**
     * Метод формирует запрос на публикацию нового объекта вещи
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя, отправившего заявку на публикацию вещи,
     *                который впоследствии будет представлен в качестве заголовка запроса
     *                к микросервису-серверу)
     * @param itemDto (объект вещи, сформированный пользователем с идентификатором userId)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект вещи с инициализированным полем-идентификатором, либо иной код ответа
     * с сообщением об ошибке.
     */
    public ResponseEntity<Object> postItem(Long userId, ItemPostDto itemDto) {
        return post("", userId, itemDto);
    }


    /**
     * Метод формирует запрос на обновление объекта вещи
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя(хозяина вещи), отправившего запрос на
     *                обновление информации об этой вещи, который впоследствии будет
     *                представлен в качестве заголовка запроса к микросервису-серверу),
     * @param itemDto (объект, содержащий обновленную информацию о вещи),
     * @param itemId (идентификатор вещи, информацию о которой необходимо обновить)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект вещи с обновленной информацией о ней, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> patchItem(Long userId, ItemPatchDto itemDto, Long itemId) {
        return patch("/" + itemId, userId, null, itemDto);
    }


    /**
     * Метод формирует запрос на получение объекта вещи
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя, отправившего запрос на
     *               получение объекта вещи, который впоследствии будет
     *               представлен в качестве заголовка запроса к микросервису-серверу)
     * @param itemId (идентификатор вещи, информацию о которой необходимо получить)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект с информацией о необходимой вещи, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }


    /**
     * Метод формирует запрос на получение объектов с информацией о вещах конкретного
     * пользователя к микросервису-серверу приложения с помощью методов
     * базового класса BaseClient.
     * @param userId (идентификатор пользователя, отправившего запрос на
     *                получение объектов вещей, который впоследствии будет
     *                представлен в качестве заголовка запроса к микросервису-серверу)
     * @param from (позиция объекта вещи в общем списке, с которого объекты
     *              включаются в результирующий набор, передаваемая в виде параметра
     *              HTTP-запроса к микросервису-серверу)
     * @param size (количество объектов вещей в результирующем наборе,
     *              передаваемое в виде параметра HTTP-запроса к микросервису-серверу)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * список объектов вещей пользователя с userId, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> getItems(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }


    /**
     * Метод формирует запрос на получение объектов с информацией о вещах по их описанию
     * к микросервису-серверу приложения с помощью методов базового класса BaseClient.
     * @param text (подстрока, которая будет использоваться для поиска объекта вещи по её
     *              наименованию и описанию),
     * @param userId (идентификатор пользователя, отправившего запрос на
     *                получение объектов вещей, который впоследствии будет
     *                представлен в качестве заголовка запроса к микросервису-серверу),
     * @param from (позиция объекта вещи в общем списке, с которого объекты
     *              включаются в результирующий набор, передаваемая в виде параметра
     *              HTTP-запроса к микросервису-серверу)
     * @param size (количество объектов вещей в результирующем наборе,
     *              передаваемое в виде параметра HTTP-запроса к микросервису-серверу)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * список объектов вещей пользователя с userId, содержащего в наименовании или описании
     * подстроку text, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> getItemsBySearch(String text, Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }


    /**
     * Метод формирует запрос на публикацию нового комментария к вещи
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param itemId (идентификатор вещи, к которой публикуется комментарий, передается
     *                в качестве переменной строки запроса),
     * @param userId (идентификатор пользователя, отправившего заявку на публикацию комментария,
     *                который впоследствии будет представлен в качестве заголовка запроса
     *                к микросервису-серверу)
     * @param commentDto (объект комментария, сформированный пользователем с идентификатором userId)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект комментария с инициализированным полем-идентификатором, либо иной код ответа
     * с сообщением об ошибке.
     */
    public ResponseEntity<Object> addComment(Long itemId, CommentDto commentDto, Long userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
