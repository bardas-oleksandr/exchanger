package ua.pb.converter.fromdto;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ua.pb.dto.create.NbuRateCreateDto;
import ua.pb.model.Currency;

/**
 *
 */
@Component("nbuRateCreateDtoToCurrencyConverter")
public class NbuRateCreateDtoToCurrencyConverter implements Converter<NbuRateCreateDto, Currency> {

    @Override
    public Currency convert(@NonNull NbuRateCreateDto source) {
        Currency currency = new Currency();
        currency.setCode(source.getCc());
        currency.setName(source.getTxt());
        return currency;
    }
}
