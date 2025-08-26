package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        validateUser(user, true);
        checkEmailUniqueness(user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            validateEmail(user.getEmail());
            if (!user.getEmail().equals(existingUser.getEmail())) {
                checkEmailUniqueness(user.getEmail());
            }
            existingUser.setEmail(user.getEmail());
        }

        return userRepository.save(existingUser);
    }

    private void validateUser(User user, boolean isCreate) {
        if (isCreate) {
            if (user.getName() == null || user.getName().isBlank()) {
                throw new IllegalArgumentException("Name cannot be blank");
            }
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new IllegalArgumentException("Email cannot be blank");
            }
        }
        validateEmail(user.getEmail());
    }

    private void validateEmail(String email) {
        if (email != null && !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void checkEmailUniqueness(String email) {
        for (User u : userRepository.findAll()) {
            if (u.getEmail().equals(email)) {
                throw new IllegalArgumentException("Email already exists");
            }
        }
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}