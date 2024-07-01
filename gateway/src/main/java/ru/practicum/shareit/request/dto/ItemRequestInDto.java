package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ItemRequestInDto {
    private Long id;
    @Size(min = 1, max = 200)
    private String description;
    private Long requestor;
}
