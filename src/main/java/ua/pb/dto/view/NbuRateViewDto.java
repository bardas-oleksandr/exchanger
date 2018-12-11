package ua.pb.dto.view;

import lombok.*;

import java.sql.Timestamp;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class NbuRateViewDto {

    private int id;
    private CurrencyViewDto currencyViewDto;
    private float price;
    private Timestamp date;

}
