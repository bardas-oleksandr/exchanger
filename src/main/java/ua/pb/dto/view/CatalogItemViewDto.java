package ua.pb.dto.view;

import lombok.*;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class CatalogItemViewDto {

    private RateViewDto rateViewDto;
    private NbuRateViewDto nbuRateViewDto;

    public CatalogItemViewDto(RateViewDto rateViewDto) {
        this.rateViewDto = rateViewDto;
    }
}
