package ua.pb.converter.todto;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.pb.dto.view.UserViewDto;
import ua.pb.model.User;

@Component("userConverter")
public class UserConverter implements Converter<User, UserViewDto> {

    @Override
    public UserViewDto convert(@NonNull User source) {
        UserViewDto userViewDto = new UserViewDto();
        userViewDto.setId(source.getId());
        userViewDto.setUsername(source.getUsername());
        userViewDto.setState(source.getState());
        return userViewDto;
    }
}
