package ua.pb.dto.create;

import lombok.*;
import ua.pb.model.User;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class UserCreateDto {

    @Size(max = 20, message = "unacceptable_username_length")
    @NotEmpty(message = "empty_username")
    private String username;

    @Size(min = 4,max = 20, message = "unacceptable_password_length")
    @NotNull(message = "empty_password")
    private String password;

    @NotNull(message = "empty_user_state")
    private User.State state;

    public UserCreateDto(String userName, String password, User.State state) {
        this.username = userName;
        this.password = password;
        this.state = state;
    }
}
