package ua.pb.dto.view;

import lombok.*;

import java.sql.Timestamp;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class OperationViewDto {

    private int id;
    private RateViewDto rateViewDto;
    private NbuRateViewDto nbuRateViewDto;
    private UserViewDto userViewDto;
    private boolean buyOperation;
    private float sumHrn;
    private float sumCurrency;
    private Timestamp date;
    private boolean deleted;
}
