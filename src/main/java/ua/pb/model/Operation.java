package ua.pb.model;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *Класс Operation представляет сущность "Операция покупки-продажи"
 *Автор: Бардась А.А.
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Operation implements Serializable {

    private static final long serialVersionUID = -7401902807654254538L;

    private int id;
    private Rate rate;
    private NbuRate nbuRate;
    private User user;
    private boolean buyOperation;   //true - покупка, false - продажа
    private float sumHrn;          //Сумма в гривне
    private float sumCurrency;     //Сумма в валюте
    private Timestamp date;
    private boolean deleted;        //Статус операции - удалена или нет

    public Operation(Rate rate, NbuRate nbuRate, User user, boolean buyOperation
            , float sumHrn, float sumCurrency, Timestamp date, boolean deleted) {
        this.rate = rate;
        this.nbuRate = nbuRate;
        this.user = user;
        this.buyOperation = buyOperation;
        this.sumHrn = sumHrn;
        this.sumCurrency = sumCurrency;
        this.date = date;
        this.deleted = deleted;
    }
}
