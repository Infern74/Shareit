package ru.practicum.shareit.service;

import ru.practicum.shareit.dto.user.UserCreateDto;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserCreateDto userCreateDto);

    UserDto updateUser(Long id, UserUpdateDto userUpdateDto);

    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    void deleteUser(Long id);
}