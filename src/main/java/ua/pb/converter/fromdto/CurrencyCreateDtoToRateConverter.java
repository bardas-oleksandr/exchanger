package ua.pb.converter.fromdto;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.pb.dto.create.CurrencyCreateDto;
import ua.pb.model.Currency;
import ua.pb.model.Rate;

@Component("currencyCreateDtoToRateConverter")
public class CurrencyCreateDtoToRateConverter implements Converter<CurrencyCreateDto, Rate> {

    @Autowired
    @Qualifier("currencyCreateDtoToCurrencyConverter")
    private CurrencyCreateDtoToCurrencyConverter toCurrencyConverter;

    @Override
    public Rate convert(@NonNull CurrencyCreateDto source) {
        Rate rate = new Rate();
        Currency currency = toCurrencyConverter.convert(source);
        rate.setCurrency(currency);
        rate.setSale(source.getSale());
        rate.setBuy(source.getBuy());
        return rate;
    }
}
