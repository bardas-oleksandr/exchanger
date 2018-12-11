package ua.pb.model;

import lombok.*;

import java.io.Serializable;

/**
 *Класс Currency представляет сущность "Валюта"
 *Автор: Бардась А.А.
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Currency implements Serializable {

    private static final long serialVersionUID = -7776767897402917729L;

    private int id;
    private String code;
    private String name;

    public Currency(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
