package ua.pb.dto.view;

import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class CurrencyViewDto {

    private int id;
    private String code;
    private String name;
}
