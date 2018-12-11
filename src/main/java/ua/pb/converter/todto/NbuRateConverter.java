package ua.pb.converter.todto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ua.pb.dto.view.CurrencyViewDto;
import ua.pb.dto.view.NbuRateViewDto;
import ua.pb.model.Currency;
import ua.pb.model.NbuRate;

/**
 *
 */
@Component("nbuRateConverter")
public class NbuRateConverter implements Converter<NbuRate, NbuRateViewDto> {

    @Autowired
    private CurrencyConverter currencyConverter;

    @Override
    public NbuRateViewDto convert(@NonNull NbuRate source) {
        NbuRateViewDto nbuRateViewDto = new NbuRateViewDto();
        nbuRateViewDto.setId(source.getId());
        Currency currency = source.getCurrency();
        CurrencyViewDto currencyViewDto = currencyConverter.convert(currency);
        nbuRateViewDto.setCurrencyViewDto(currencyViewDto);
        nbuRateViewDto.setDate(source.getDate());
        nbuRateViewDto.setPrice(source.getPrice());
        return nbuRateViewDto;
    }
}
