package ru.practicum.shareit.datajpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

@DataJpaTest
public class UserJpaRepositoryTest {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    public void testPostUserOk() {
        User user = new User(null, "Petr Petrov", "petrpetrov@gmail.com");
        Assertions.assertNull(user.getId());
        userJpaRepository.save(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(user.getId(), user.getId());
        Assertions.assertEquals(user.getName(), "Petr Petrov");
        Assertions.assertEquals(user.getEmail(), "petrpetrov@gmail.com");
    }
}
