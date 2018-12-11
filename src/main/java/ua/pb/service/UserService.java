package ua.pb.service;

import lombok.NonNull;
import ua.pb.dto.create.UserCreateDto;
import ua.pb.dto.create.UserUpdateDto;
import ua.pb.dto.view.UserViewDto;
import ua.pb.model.User;

import java.util.List;

public interface UserService {

    UserViewDto create(@NonNull UserCreateDto userCreateDto);

    UserViewDto update(@NonNull UserUpdateDto userUpdateDto, int userId);

    void delete(int userId);

    UserViewDto getUserById(int userId);

    List<UserViewDto> getAllUsers();

    //Возвращаем объект User а не UserViewDto для нужд Spring Security
    User getUserByUsername(@NonNull String username);
}
