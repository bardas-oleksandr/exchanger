package ua.pb.model;

import lombok.*;

import java.io.Serializable;

/**
 *Класс User представляет сущность "Пользователь"
 *Автор: Бардась А.А.
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = -6139035153060382268L;

    private int id;
    private String username;
    private String password;
    private State state;

    public User(String username, String password, State state) {
        this.username = username;
        this.password = password;
        this.state = state;
    }

    public enum State {
        ADMIN, OPERATOR;

        public static State get(int stateIndex) {
            switch (stateIndex) {
                case 0:
                    return ADMIN;
                case 1:
                    return OPERATOR;
                default:
                    throw new IllegalArgumentException("Unexpected user state index.");
            }
        }
    }
}
