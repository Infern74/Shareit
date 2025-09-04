package ru.practicum.shareit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.user.UserCreateDto;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.dto.user.UserUpdateDto;
import ru.practicum.shareit.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserCreateDto userCreateDto) {
        log.info("POST /users - Создание пользователя: {}", userCreateDto);
        UserDto result = userService.createUser(userCreateDto);
        log.info("Пользователь создан успешно: {}", result);
        return result;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto userUpdateDto) {
        log.info("PATCH /users/{} - Обновление пользователя: {}", id, userUpdateDto);
        UserDto result = userService.updateUser(id, userUpdateDto);
        log.info("Пользователь обновлен успешно: {}", result);
        return result;
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("GET /users/{} - Получение пользователя по ID", id);
        UserDto result = userService.getUserById(id);
        log.info("Найден пользователь: {}", result);
        return result;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET /users - Получение всех пользователей");
        List<UserDto> result = userService.getAllUsers();
        log.info("Найдено {} пользователей", result.size());
        return result;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("DELETE /users/{} - Удаление пользователя", id);
        userService.deleteUser(id);
        log.info("Пользователь с ID {} удален успешно", id);
    }
}