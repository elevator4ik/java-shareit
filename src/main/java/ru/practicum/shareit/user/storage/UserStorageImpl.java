package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserStorageImpl implements UserStorage {

    private Map<Integer, User> userStorage = new HashMap<>();
    private int id = 1;

    @Override
    public List<User> getAllUsers() {
        if (userStorage != null && !userStorage.isEmpty()) {
            return new ArrayList<>(userStorage.values());
        } else {
            return null;
        }
    }

    @Override
    public User getUserById(int id) {
        if (userStorage != null &&
                !userStorage.isEmpty() &&
                userStorage.containsKey(id)) {
            return userStorage.get(id);
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    public User add(User user) {

        if (userMailDuplicateCheck(user)) {
            user.setId(id);
            id++;
            userStorage.put(user.getId(), user);
            return userStorage.get(user.getId()); //цепляем из хранилища, чтобы сразу подтвердить корректную запись
        } else {
            return null;
        }
    }

    @Override
    public User update(User user) {

        if (userMailDuplicateCheck(user)) {
            userStorage.replace(user.getId(), user);
            return userStorage.get(user.getId());
        } else {
            return null;
        }
    }

    @Override
    public void delete(int id) {
        userStorage.remove(id);
    }

    private boolean userMailDuplicateCheck(User user) {
        boolean b = true;
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Email is invalid");
        } else {
            if (userStorage != null && !userStorage.isEmpty()) {
                for (User u : userStorage.values()) {
                    if (u.getEmail().equals(user.getEmail())) {
                        if (!u.getId().equals(user.getId())) {
                            b = false;
                            break;
                        }
                    }
                }
            }
        }
        return b;
    }
}
