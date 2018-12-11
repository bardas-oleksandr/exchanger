package ua.pb.converter.fromdto;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.pb.dto.create.UserCreateDto;
import ua.pb.model.User;

/**
 *
 */
@Component("userCreateDtoConverter")
public class UserCreateDtoConverter implements Converter<UserCreateDto, User> {

    @Override
    public User convert(@NonNull UserCreateDto source) {
        User user = new User();
        user.setUsername(source.getUsername());
        user.setPassword(source.getPassword());
        user.setState(source.getState());
        return user;
    }
}