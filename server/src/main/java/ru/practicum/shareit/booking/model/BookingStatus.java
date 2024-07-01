package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Перечисление BookingStatus содержит все возможные состояния бронирования,
 * исключая возможность внесения неверного состояния или поиска по неизвестному состоянию.
 */
@AllArgsConstructor
@Getter
public enum BookingStatus {
    WAITING("WAITING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    PAST("PAST"),
    CANCELED("CANCELED");

    private String description;
}
