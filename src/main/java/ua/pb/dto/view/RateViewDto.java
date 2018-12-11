package ua.pb.dto.view;

import lombok.*;

import java.sql.Timestamp;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class RateViewDto {

    private int id;
    private CurrencyViewDto currencyViewDto;
    private Timestamp date;
    private float sale;
    private float buy;

}
