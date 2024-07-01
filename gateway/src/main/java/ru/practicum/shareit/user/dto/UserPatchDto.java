package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@Getter
@Setter
public class UserPatchDto {
    private final String name;
    @Size(min = 1)
    @Email(message = "Неверный формат электронной почты!")
    private final String email;
}
