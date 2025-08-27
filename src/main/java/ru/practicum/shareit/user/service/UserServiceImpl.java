package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserCreateDto userCreateDto) {
        log.info("Создание пользователя: {}", userCreateDto);
        User user = UserMapper.toUser(userCreateDto);

        if (userRepository.existsByEmail(user.getEmail())) {
            String errorMessage = String.format("Email %s уже существует", user.getEmail());
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        User savedUser = userRepository.save(user);
        log.info("Пользователь создан: {}", savedUser);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        log.info("Обновление пользователя ID: {}, данные: {}", id, userUpdateDto);
        User existingUser = getUserByIdOrThrow(id);

        if (userUpdateDto.getName() != null) {
            existingUser.setName(userUpdateDto.getName());
        }

        if (userUpdateDto.getEmail() != null) {
            if (userRepository.existsByEmailAndIdNot(userUpdateDto.getEmail(), id)) {
                String errorMessage = String.format("Email %s уже существует", userUpdateDto.getEmail());
                log.error(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
            existingUser.setEmail(userUpdateDto.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("Пользователь обновлен: {}", updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        log.debug("Поиск пользователя по ID: {}", id);
        User user = getUserByIdOrThrow(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.debug("Получение всех пользователей");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Удаление пользователя с ID: {}", id);
        getUserByIdOrThrow(id);
        userRepository.deleteById(id);
        log.info("Пользователь с ID {} удален", id);
    }

    private User getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь с ID %d не найден", id);
                    log.error(errorMessage);
                    return new NoSuchElementException(errorMessage);
                });
    }
}