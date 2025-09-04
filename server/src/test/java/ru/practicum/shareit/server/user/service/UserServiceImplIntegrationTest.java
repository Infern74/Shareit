package ru.practicum.shareit.server.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.dto.user.UserCreateDto;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.dto.user.UserUpdateDto;
import ru.practicum.shareit.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser() {
        UserCreateDto userCreateDto = new UserCreateDto("test", "test@test.com");
        UserDto userDto = userService.createUser(userCreateDto);

        assertNotNull(userDto.getId());
        assertEquals("test", userDto.getName());
        assertEquals("test@test.com", userDto.getEmail());
    }

    @Test
    void updateUser() {
        UserCreateDto userCreateDto = new UserCreateDto("test", "test@test.com");
        UserDto userDto = userService.createUser(userCreateDto);

        UserUpdateDto updateDto = new UserUpdateDto("updated", "updated@test.com");
        UserDto updatedUser = userService.updateUser(userDto.getId(), updateDto);

        assertEquals("updated", updatedUser.getName());
        assertEquals("updated@test.com", updatedUser.getEmail());
    }
}