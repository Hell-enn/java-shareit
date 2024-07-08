package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPatchDto;

/**
 * Класс UserClient - клиентский слой микросервиса-шлюза приложения,
 * отвечающий за преобразование маршрутизированных из слоя-контроллера
 * объектов пользователей и прочих необходимых объектов и идентификаторов
 * в HTTP-запросы к микросервису-серверу, где содержится основная бизнес-логика
 * приложения.
 * Наследует от базового класса BaseClient, где инкапсулирована логика формирования
 * всех HTTP-запросов к серверному микросервису приложения, и использует его методы
 * в основе собственных специализированных.
 * Поля:
 *  API_PREFIX - строковая константа, используемая при создании объекта типа RestTemplate
 *  в качестве префикса к адресу микросервиса-сервера, для формирования запросов,
 *  касающихся пользователей.
 */
@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    /**
     * Метод формирует запрос на публикацию нового объекта пользователя
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userPostDto (объект, содержащий информацию о новом пользователе)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект пользователя с инициализированным полем-идентификатором, либо иной код ответа
     * с сообщением об ошибке.
     */
    public ResponseEntity<Object> postUser(UserDto userPostDto) {
        return post("", userPostDto);
    }


    /**
     * Метод формирует запрос на обновление объекта пользователя
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя, отправившего запрос на
     *                обновление информации о себе, который впоследствии будет
     *                представлен в качестве заголовка запроса к микросервису-серверу),
     * @param userPatchDto (объект, содержащий обновленную информацию о пользователе).
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект пользователя с уже обновленными полями, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> patchUser(Long userId, UserPatchDto userPatchDto) {
        return patch("/" + userId, userPatchDto);
    }


    /**
     * Метод формирует запрос на получение объекта с информацией о пользователе
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя, информацию о котором необходимо получить)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * объект необходимого пользователя, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> getUser(Long userId) {
        return get("/" + userId);
    }


    /**
     * Метод формирует запрос на получение объектов всех пользователей приложения
     * к микросервису-серверу приложения с помощью методов базового класса BaseClient.
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2** и
     * список объектов пользователей, либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> getUsers() {
        return get("");
    }


    /**
     * Метод формирует запрос на удаление объекта с информацией о пользователе
     * к микросервису-серверу приложения с помощью методов базового класса
     * BaseClient.
     * @param userId (идентификатор пользователя, информацию о котором необходимо удалить)
     *
     * @return ResponseEntity<Object> - ответ сервера, содержащий либо код ответа 2**,
     * либо иной код ответа с сообщением об ошибке.
     */
    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId);
    }

}
