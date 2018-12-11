package ua.pb.converter.fromdto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ua.pb.dto.create.NbuRateCreateDto;
import ua.pb.model.Currency;
import ua.pb.model.NbuRate;

import java.util.Properties;

/**
 *
 */
@Component("nbuRateCreateDtoConverter")
public class NbuRateCreateDtoConverter implements Converter<NbuRateCreateDto, NbuRate> {

    @Autowired
    @Qualifier("nbuRateCreateDtoToCurrencyConverter")
    private NbuRateCreateDtoToCurrencyConverter toCurrencyConverter;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    @Override
    public NbuRate convert(@NonNull NbuRateCreateDto source) {
        NbuRate nbuRate = new NbuRate();
        Currency currency = toCurrencyConverter.convert(source);
        nbuRate.setCurrency(currency);
        nbuRate.setPrice(source.getRate());
        return nbuRate;
    }
}
