package ua.pb.service.impl;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ua.pb.dao.UserDao;
import ua.pb.dto.create.UserCreateDto;
import ua.pb.dto.create.UserUpdateDto;
import ua.pb.dto.view.UserViewDto;
import ua.pb.model.User;
import ua.pb.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserViewDto create(@NonNull UserCreateDto userCreateDto) {
        userCreateDto.setPassword(bCryptPasswordEncoder.encode(userCreateDto.getPassword()));
        User user = conversionService.convert(userCreateDto, User.class);
        userDao.create(user);
        return conversionService.convert(user, UserViewDto.class);
    }

    @Override
    public UserViewDto update(@NonNull UserUpdateDto userUpdateDto, int userId) {
        User user = conversionService.convert(userUpdateDto, User.class);
        user.setId(userId);
        userDao.update(user);
        return conversionService.convert(user, UserViewDto.class);
    }

    @Override
    public void delete(int userId) {
        userDao.delete(userId);
    }

    @Override
    public UserViewDto getUserById(int userId) {
        User user = userDao.getById(userId);
        return conversionService.convert(user, UserViewDto.class);
    }

    @Override
    public List<UserViewDto> getAllUsers() {
        List<User> userList = userDao.getAll();
        return userList.stream()
                .map((user)->conversionService.convert(user, UserViewDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.getByUsername(username);
    }
}
