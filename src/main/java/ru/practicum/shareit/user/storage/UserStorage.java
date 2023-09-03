package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User getUserById(int id);

    User add(User user);

    User update(User user);

    void delete(int id);
}
