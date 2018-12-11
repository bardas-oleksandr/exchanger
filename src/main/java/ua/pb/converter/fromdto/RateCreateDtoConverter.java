package ua.pb.converter.fromdto;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.pb.dto.create.RateCreateDto;
import ua.pb.model.Currency;
import ua.pb.model.Rate;

/**
 *
 */
@Component("rateCreateDtoConverter")
public class RateCreateDtoConverter implements Converter<RateCreateDto, Rate> {

    @Override
    public Rate convert(@NonNull RateCreateDto source) {
        Rate rate = new Rate();
        Currency currency = new Currency();
        currency.setCode(source.getCcy());
        rate.setCurrency(currency);
        rate.setBuy(source.getBuy());
        rate.setSale(source.getSale());
        return rate;
    }
}
