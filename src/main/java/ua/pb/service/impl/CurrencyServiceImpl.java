package ua.pb.service.impl;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ua.pb.dao.CurrencyDao;
import ua.pb.dao.RateDao;
import ua.pb.dto.create.CurrencyCreateDto;
import ua.pb.dto.view.CurrencyViewDto;
import ua.pb.model.Currency;
import ua.pb.model.Rate;
import ua.pb.service.CurrencyService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("currencyService")
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private RateDao rateDao;

    @Autowired
    private ConversionService conversionService;

    @Override
    public CurrencyViewDto create(@NonNull CurrencyCreateDto currencyCreateDto) {
        Currency currency = conversionService.convert(currencyCreateDto, Currency.class);
        currencyDao.create(currency);
        Rate rate = conversionService.convert(currencyCreateDto, Rate.class);
        rate.setCurrency(currency);
        rate.setDate(new Timestamp(System.currentTimeMillis()));
        rateDao.create(rate);
        return conversionService.convert(currency, CurrencyViewDto.class);
    }

    @Override
    public CurrencyViewDto update(@NonNull CurrencyCreateDto currencyCreateDto, int currencyId) {
        Currency currency = conversionService.convert(currencyCreateDto, Currency.class);
        currency.setId(currencyId);
        currencyDao.update(currency);
        Rate rate = conversionService.convert(currencyCreateDto, Rate.class);
        rate.setCurrency(currency);
        rate.setDate(new Timestamp(System.currentTimeMillis()));
        rateDao.create(rate);
        return conversionService.convert(currency, CurrencyViewDto.class);
    }

    @Override
    public void delete(int currencyId) {
        currencyDao.delete(currencyId);
    }

    @Override
    public CurrencyViewDto getById(int currencyId) {
        Currency currency = currencyDao.getById(currencyId);
        return conversionService.convert(currency, CurrencyViewDto.class);
    }

    @Override
    public List<CurrencyViewDto> getAll() {
        List<Currency> currencyList = currencyDao.getAll();
        return currencyList.stream()
                .map((currency)->conversionService
                        .convert(currency, CurrencyViewDto.class))
                .collect(Collectors.toList());
    }
}
