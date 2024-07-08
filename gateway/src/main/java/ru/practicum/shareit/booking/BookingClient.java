package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;

/**
 * Класс BookingClient - клиентский слой микросервиса-шлюза приложения,
 * отвечающий за преобразование маршрутизированных из слоя-контроллера
 * объектов бронирований и прочих необходимых объектов и идентификаторов
 * в HTTP-запросы к микросервису-серверу, где содержится основная бизнес-логика
 * приложения.
 * Наследует от базового класса BaseClient, где инкапсулирована логика формирования
 * всех HTTP-запросов к серверному микросервису приложения, и использует его методы
 * в основе собственных специализированных.
 * Поля:
 *  API_PREFIX - строковая константа, используемая при создании объекта типа RestTemplate
 *  в качестве префикса к адресу микросервиса-сервера, для формирования запросов,
 *  касающихся бронирований.
 */
@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    /**
     * Метод формирует запрос на публикацию нового объекта бронирования
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя, отправившего заявку на бронирование,
     *                который впоследствии будет представлен в качестве заголовка запроса
     *                к микросервису-серверу)
     * @param bookingDto (объект бронирования(заявка), сформированный пользователем с
     *                идентификатором userId)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект бронирования с инициализированным полем-идентификатором, либо иной код ответа
     * с сообщением об ошибке.
     */
    public ResponseEntity<Object> postBooking(Long userId, BookingDto bookingDto) {
        return post("", userId, bookingDto);
    }


    /**
     * Метод формирует запрос на обновление статуса объекта бронирования
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя(хозяина вещи), отправившего запрос на
     *                обновление статуса бронирования, который впоследствии будет
     *                представлен в качестве заголовка запроса к микросервису-серверу)
     * @param approved (переменная, содержащая информацию о решении хозяина вещи о судьбе
     *                бронирования. true - подтверждено, false - отклонено)
     * @param bookingId (идентификатор бронирования, чей статус необходимо обновить)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект обновленного бронирования, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> patchBooking(Long userId, Boolean approved, Long bookingId) {
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }


    /**
     * Метод формирует запрос на получение объекта бронирования
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя, отправившего запрос на
     *               получение объекта бронирования, который впоследствии будет
     *               представлен в качестве заголовка запроса к микросервису-серверу)
     * @param bookingId (идентификатор бронирования, которое необходимо получить)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект необходимого бронирования, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }


    /**
     * Метод формирует запрос на получение объектов бронирований конкретного
     * пользователя к микросервису-серверу приложения с помощью методов
     * базового класса BaseClient.
     * @param userId (идентификатор пользователя, отправившего запрос на
     *                получение объектов бронирований, который впоследствии будет
     *                представлен в качестве заголовка запроса к микросервису-серверу)
     * @param state (состояние, по которому будут отобраны объекты бронирований,
     *               передаваемое в виде параметра HTTP-запроса к микросервису-серверу)
     * @param from (позиция объекта бронирования в общем списке, с которого объекты
     *              включаются в результирующий набор, передаваемая в виде параметра
     *              HTTP-запроса к микросервису-серверу)
     * @param size (количество объектов бронирований в результирующем наборе,
     *              передаваемое в виде параметра HTTP-запроса к микросервису-серверу)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * список объектов бронирований пользователя с userId в статусе state,
     * либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> getBookings(Long userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }


    /**
     * Метод формирует запрос на получение объектов бронирований вещей конкретного
     * пользователя к микросервису-серверу приложения с помощью методов
     * базового класса BaseClient.
     * @param userId (идентификатор пользователя, отправившего запрос на
     *                получение объектов бронирований своих вещей, который впоследствии будет
     *                представлен в качестве заголовка запроса к микросервису-серверу)
     * @param state (состояние, по которому будут отобраны объекты бронирований,
     *               передаваемое в виде параметра HTTP-запроса к микросервису-серверу)
     * @param from (позиция объекта бронирования в общем списке, с которого объекты
     *              включаются в результирующий набор, передаваемая в виде параметра
     *              HTTP-запроса к микросервису-серверу)
     * @param size (количество объектов бронирований в результирующем наборе,
     *              передаваемое в виде параметра HTTP-запроса к микросервису-серверу)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * список объектов бронирований вещей пользователя с userId в статусе state,
     * либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> getUserStuffBookings(Long userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }
}