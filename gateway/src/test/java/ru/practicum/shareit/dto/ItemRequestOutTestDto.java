package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ItemRequestOutTestDto {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
    private List<ItemResponseTestDto> items;
}
