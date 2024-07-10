package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestPatchDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;

import java.util.Map;

/**
 * Класс RequestClient - клиентский слой микросервиса-шлюза приложения,
 * отвечающий за преобразование маршрутизированных из слоя-контроллера
 * объектов запросов вещей и прочих необходимых объектов и идентификаторов
 * в HTTP-запросы к микросервису-серверу, где содержится основная бизнес-логика
 * приложения.
 * Наследует от базового класса BaseClient, где инкапсулирована логика формирования
 * всех HTTP-запросов к серверному микросервису приложения, и использует его методы
 * в основе собственных специализированных.
 * Поля:
 *  API_PREFIX - строковая константа, используемая при создании объекта типа RestTemplate
 *  в качестве префикса к адресу микросервиса-сервера, для формирования запросов,
 *  касающихся запросов вещей.
 */
@Service
public class RequestClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    /**
     * Метод формирует запрос на публикацию нового объекта запроса вещи
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя, отправившего запрос вещи,
     *                который впоследствии будет представлен в качестве заголовка запроса
     *                к микросервису-серверу)
     * @param itemRequestInDto (объект запроса вещи, сформированный пользователем с
     *                идентификатором userId)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект запроса вещи с инициализированным полем-идентификатором, либо иной код ответа
     * с сообщением об ошибке.
     */
    public ResponseEntity<Object> postItemRequest(Long userId, ItemRequestPostDto itemRequestInDto) {
        return post("", userId, itemRequestInDto);
    }


    /**
     * Метод формирует запрос на обновление информации об объекте запроса вещи
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя, отправившего запрос на
     *                обновление запроса вещи, который впоследствии будет
     *                представлен в качестве заголовка запроса к микросервису-серверу),
     * @param itemRequestInDto (объект, содержащий обновленную информацию о запросе вещи),
     * @param requestId (идентификатор запроса вещи, информацию о котором необходимо обновить)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект обновленного запроса вещи, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> patchItemRequest(Long userId, ItemRequestPatchDto itemRequestInDto, Long requestId) {
        return patch("/" + requestId, userId, null, itemRequestInDto);
    }


    /**
     * Метод формирует запрос на получение объекта запроса вещи
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя, отправившего запрос на
     *               получение объекта запроса вещи, который впоследствии будет
     *               представлен в качестве заголовка запроса к микросервису-серверу)
     * @param requestId (идентификатор запроса вещи, который необходимо получить)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект необходимого запроса вещи, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> getItemRequest(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }


    /**
     * Метод формирует запрос на получение объектов запросов вещей конкретного
     * пользователя к микросервису-серверу приложения с помощью методов
     * базового класса BaseClient.
     * @param userId (идентификатор пользователя, отправившего запрос на
     *                получение объектов своих запросов вещей, который впоследствии будет
     *                представлен в качестве заголовка запроса к микросервису-серверу).
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * список объектов запросов вещей пользователя с userId,
     * либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> getItemRequests(Long userId) {
        return get("", userId);
    }


    /**
     * Метод формирует запрос на получение объектов запросов вещей к микросервису-серверу
     * приложения с помощью методов базового класса BaseClient.
     * @param userId (идентификатор пользователя, отправившего запрос на
     *                получение объектов запросов вещей, который впоследствии будет
     *                представлен в качестве заголовка запроса к микросервису-серверу),
     * @param from (позиция объекта запроса вещи в общем списке, с которого объекты
     *              включаются в результирующий набор, передаваемая в виде параметра
     *              HTTP-запроса к микросервису-серверу),
     * @param size (количество объектов запросов вещей в результирующем наборе,
     *              передаваемое в виде параметра HTTP-запроса к микросервису-серверу).
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * список объектов запросов вещей, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> getAllItemRequests(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}
