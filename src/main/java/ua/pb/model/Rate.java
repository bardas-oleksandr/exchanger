package ua.pb.model;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *Класс Rate представляет сущность "Курс Приватбанка"
 *Автор: Бардась А.А.
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Rate implements Serializable {

    private static final long serialVersionUID = -2044385061926656205L;

    private int id;
    private Currency currency;
    private Timestamp date;
    private float sale;
    private float buy;

    public Rate(Currency currency, Timestamp date, float sale, float buy) {
        this.currency = currency;
        this.date = date;
        this.sale = sale;
        this.buy = buy;
    }
}
