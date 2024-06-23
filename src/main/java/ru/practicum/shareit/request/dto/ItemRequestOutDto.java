package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ItemRequestOutDto {
    private Long id;
    @Size(min = 1, max = 200)
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<ItemResponseDto> items;
}
