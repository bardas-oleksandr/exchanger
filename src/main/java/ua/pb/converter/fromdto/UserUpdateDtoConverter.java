package ua.pb.converter.fromdto;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.pb.dto.create.UserUpdateDto;
import ua.pb.model.User;

/**
 *
 */
@Component("userUpdateDtoConverter")
public class UserUpdateDtoConverter implements Converter<UserUpdateDto, User> {

    @Override
    public User convert(@NonNull UserUpdateDto source) {
        User user = new User();
        user.setUsername(source.getUsername());
        user.setState(source.getState());
        return user;
    }
}