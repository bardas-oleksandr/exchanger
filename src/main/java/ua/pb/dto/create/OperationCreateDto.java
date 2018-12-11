package ua.pb.dto.create;

import lombok.*;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class OperationCreateDto {

    private Integer rateId;

    private Integer nbuRateId;

    @NotNull(message = "empty_buyOperation_field")
    private Boolean buyOperation;

    private Float sumHrn;

    private Float sumCurrency;

    @AssertTrue(message = "unacceptable_rate_id")
    public boolean isValidRateId(){
        return rateId != null && rateId > 0;
    }

    @AssertTrue(message = "unacceptable_nbu_rate_id")
    public boolean isValidNbuRateId(){
        return nbuRateId != null && nbuRateId > 0;
    }

    @AssertTrue(message = "unacceptable_sumHrn")
    public boolean isValidSumHrn(){
        return sumHrn != null && sumHrn >= 0.0f;
    }

    @AssertTrue(message = "unacceptable_sumCurrency")
    public boolean isValidSumCurrency(){
        return sumCurrency != null && sumCurrency >= 0.0f;
    }

    public OperationCreateDto(Integer rateId, Integer nbuRateId
            , @NotNull(message = "empty_buyOperation_field") Boolean buyOperation
            , Float sumHrn, Float sumCurrency) {
        this.rateId = rateId;
        this.nbuRateId = nbuRateId;
        this.buyOperation = buyOperation;
        this.sumHrn = sumHrn;
        this.sumCurrency = sumCurrency;
    }
}
