package ua.pb.dto.create;

import lombok.*;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class CurrencyCreateDto {

    @NotEmpty(message = "empty_currency_code")
    private String code;

    @NotEmpty(message = "empty_currency_name")
    private String name;

    private Float buy;

    private Float sale;

    @AssertTrue(message = "sale_rate_is_not_bigger_then_buy_rate")
    public boolean saleRateIsBiggerThenBuyRate(){
        return sale != null
                && buy != null
                && sale - buy > 0;
    }

    @AssertTrue(message = "unacceptable_sale_price")
    public boolean isCorrectSale() {
        return sale != null && sale > 0.0;
    }

    @AssertTrue(message = "unacceptable_buy_price")
    public boolean isCorrectBuy() {
        return buy != null && buy > 0.0;
    }

    public CurrencyCreateDto(@NotEmpty(message = "empty_currency_code") String code
            , @NotEmpty(message = "empty_currency_name") String name, Float buy, Float sale) {
        this.code = code;
        this.name = name;
        this.buy = buy;
        this.sale = sale;
    }
}
