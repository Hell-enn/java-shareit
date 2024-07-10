package ru.practicum.shareit.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.controller.BookingServerController;
import ru.practicum.shareit.item.controller.ItemServerController;
import ru.practicum.shareit.request.controller.RequestServerController;
import ru.practicum.shareit.user.controller.UserServerController;


/**
 * Класс-обработчик ErrorHandler предназначен для
 * обработки ошибок на стороне сервера и отправки правильного
 * кода ответа на клиентскую сторону с детальным описанием
 * причин возникшей проблемы.
 */
@RestControllerAdvice(assignableTypes = {UserServerController.class, ItemServerController.class, RequestServerController.class, BookingServerController.class})
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleBadRequest(final BadRequestException e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    public ResponseEntity<Object> handleNotFound(final NotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler
    public ResponseEntity<Object> handleForbidden(final ForbiddenException e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler
    public ResponseEntity<Object> handleValidation(final ValidationException e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    public ResponseEntity<Object>  handleSpringValidation(MethodArgumentNotValidException e) {
        return new ResponseEntity<>("Объект не прошел валидацию!", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    public ResponseEntity<Object>  handleSqlException(DataIntegrityViolationException e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT);
    }


    @ExceptionHandler
    public ResponseEntity<Object> handleUnsupportedOperationException(final UnsupportedOperationException e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    public ResponseEntity<Object> handleAnyError(final Throwable e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
