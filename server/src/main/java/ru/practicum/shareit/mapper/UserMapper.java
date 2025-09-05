package ru.practicum.shareit.mapper;

import ru.practicum.shareit.dto.user.UserCreateDto;
import ru.practicum.shareit.dto.user.UserDto;
import ru.practicum.shareit.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toUser(UserCreateDto userCreateDto) {
        User user = new User();
        user.setName(userCreateDto.getName());
        user.setEmail(userCreateDto.getEmail());
        return user;
    }
}