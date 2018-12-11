package ua.pb.model;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *Класс NbuRate представляет сущность "Курс НБУ"
 *Автор: Бардась А.А.
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class NbuRate implements Serializable {

    private static final long serialVersionUID = 4331983211414049962L;

    private int id;
    private Currency currency;
    private float price;
    private Timestamp date;

    public NbuRate(Currency currency, float price, Timestamp date) {
        this.currency = currency;
        this.price = price;
        this.date = date;
    }
}
