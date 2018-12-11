package ua.pb.converter.fromdto;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.pb.dto.create.CurrencyCreateDto;
import ua.pb.model.Currency;

/**
 *
 */
@Component("currencyCreateDtoToCurrencyConverter")
public class CurrencyCreateDtoToCurrencyConverter implements Converter<CurrencyCreateDto, Currency> {

    @Override
    public Currency convert(@NonNull CurrencyCreateDto source) {
        Currency currency = new Currency();
        currency.setCode(source.getCode());
        currency.setName(source.getName());
        return currency;
    }
}
