package ua.pb.dto.create;

import lombok.*;
import ua.pb.model.User;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class UserUpdateDto {

    @Size(max = 20, message = "unacceptable_username_length")
    @NotEmpty(message = "empty_username")
    private String username;

    @NotNull(message = "empty_user_state")
    private User.State state;

    public UserUpdateDto(String userName, User.State state) {
        this.username = userName;
        this.state = state;
    }
}
