package ua.pb.converter.todto;

import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.pb.dto.view.CurrencyViewDto;
import ua.pb.model.Currency;

/**
 *
 */
@Component("currencyConverter")
public class CurrencyConverter implements Converter<Currency, CurrencyViewDto> {

    @Override
    public CurrencyViewDto convert(@NonNull Currency source) {
        CurrencyViewDto currencyViewDto = new CurrencyViewDto();
        currencyViewDto.setId(source.getId());
        currencyViewDto.setCode(source.getCode());
        currencyViewDto.setName(source.getName());
        return currencyViewDto;
    }
}
