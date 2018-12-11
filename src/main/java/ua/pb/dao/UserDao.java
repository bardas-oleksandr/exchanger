package ua.pb.dao;

import lombok.NonNull;
import ua.pb.model.User;

import java.util.List;

public interface UserDao {

    void create(@NonNull User user);

    void update(@NonNull User user);

    void delete(int id);

    User getById(int id);

    User getByUsername(@NonNull String username);

    List<User> getAll();
}
