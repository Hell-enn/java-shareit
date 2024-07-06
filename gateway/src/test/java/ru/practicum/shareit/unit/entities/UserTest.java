package ru.practicum.shareit.unit.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "email", unique = true)
    private String email;
}