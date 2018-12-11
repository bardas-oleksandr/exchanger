package ua.pb.dto.view;

import lombok.*;
import ua.pb.model.User;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class UserViewDto {

    private int id;
    private String username;
    private User.State state;
}
