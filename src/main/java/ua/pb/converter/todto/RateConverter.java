package ua.pb.converter.todto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ua.pb.dto.view.CurrencyViewDto;
import ua.pb.dto.view.RateViewDto;
import ua.pb.model.Currency;
import ua.pb.model.Rate;

/**
 *
 */
@Component("rateConverter")
public class RateConverter implements Converter<Rate,RateViewDto> {

    @Autowired
    private CurrencyConverter currencyConverter;

    @Override
    public RateViewDto convert(@NonNull Rate source) {
        RateViewDto rateViewDto = new RateViewDto();
        rateViewDto.setId(source.getId());
        Currency currency = source.getCurrency();
        CurrencyViewDto currencyViewDto = currencyConverter.convert(currency);
        rateViewDto.setCurrencyViewDto(currencyViewDto);
        rateViewDto.setDate(source.getDate());
        rateViewDto.setBuy(source.getBuy());
        rateViewDto.setSale(source.getSale());
        return rateViewDto;
    }
}
