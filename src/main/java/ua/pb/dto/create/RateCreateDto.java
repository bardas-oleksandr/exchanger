package ua.pb.dto.create;

import lombok.*;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class RateCreateDto {

    @NotEmpty(message = "empty_ccy")
    private String ccy;

    @NotEmpty(message = "empty_base_ccy")
    private String base_ccy;

    private Float buy;

    private Float sale;

    @AssertTrue(message = "sale_rate_is_not_bigger_then_buy_rate")
    public boolean saleRateIsBiggerThenBuyRate(){
        return sale - buy > 0;
    }

    @AssertTrue(message = "unacceptable_sale_price")
    public boolean isCorrectSale() {
        return sale != null && sale > 0.0;
    }

    @AssertTrue(message = "unacceptable_buy_price")
    public boolean isCorrectBuy() {
        return buy != null && buy > 0.0;
    }

    public RateCreateDto(@NotEmpty(message = "empty_ccy") String ccy, @NotEmpty(message = "empty_base_ccy") String base_ccy, Float buy, Float sale) {
        this.ccy = ccy;
        this.base_ccy = base_ccy;
        this.buy = buy;
        this.sale = sale;
    }
}
