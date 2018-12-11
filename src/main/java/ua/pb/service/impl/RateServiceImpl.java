package ua.pb.service.impl;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ua.pb.dao.CurrencyDao;
import ua.pb.dao.RateDao;
import ua.pb.dto.create.RateCreateDto;
import ua.pb.dto.view.RateViewDto;
import ua.pb.model.Currency;
import ua.pb.model.Rate;
import ua.pb.service.RateService;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service("rateService")
public class RateServiceImpl implements RateService {

    @Autowired
    private RateDao rateDao;

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private ConversionService conversionService;

    @Override
    public RateViewDto create(@NonNull RateCreateDto rateCreateDto) {
        Rate rate = conversionService.convert(rateCreateDto, Rate.class);
        rate.setDate(new Timestamp(System.currentTimeMillis()));
        rate.setCurrency(currencyDao.getByCode(rate.getCurrency().getCode()));
        rateDao.create(rate);
        return conversionService.convert(rate, RateViewDto.class);
    }

    @Override
    public RateViewDto update(@NonNull RateCreateDto rateCreateDto, int rateId) {
        Rate rate = conversionService.convert(rateCreateDto, Rate.class);
        rate.setId(rateId);
        rate.setDate(new Timestamp(System.currentTimeMillis()));
        rate.setCurrency(currencyDao.getByCode(rate.getCurrency().getCode()));
        rateDao.update(rate);
        return conversionService.convert(rate, RateViewDto.class);
    }

    @Override
    public void delete(int rateId) {
        rateDao.delete(rateId);
    }

    @Override
    public RateViewDto getById(int rateId) {
        Rate rate = rateDao.getById(rateId);
        return conversionService.convert(rate, RateViewDto.class);
    }

    /**
     * Метод выполняет добавление информации о новых курсах валют
     * только для тех видов валюты, которая уже имеется в базе данных
     */
    @Override
    public void addAllForExistingCurrencies(List<RateCreateDto> rateCreatewDtoList) {
        //Оставлем только ту валюту, которая продается за гривны
        List<RateCreateDto> tradedForUAH = rateCreatewDtoList.stream()
                .filter((rate)->rate.getBase_ccy().equals("UAH"))
                .collect(Collectors.toList());

        //Конвертация
        List<Rate> rateList = tradedForUAH.stream()
                .map((rateCreateDto) -> conversionService
                        .convert(rateCreateDto, Rate.class))
                .collect(Collectors.toList());

        //Список валют, которые есть как в базе данных, так и в списке rateList
        List<Currency> currencyList = getMatchingCurrencies(rateList);

        //Из списка rateList получаем новый список объектов Rate,
        //валюта которых есть в базе данных.
        List<Rate> matchingRateList = rateList.stream().filter((rate) -> currencyList.stream()
                .anyMatch((currency -> currency.getCode()
                        .equals(rate.getCurrency().getCode()))))
                .collect(Collectors.toList());

        //Обновляем поля Currency для объектов из списка rateList.
        //После обновления нам становятся доступны не только значения
        //кодов валют, но и их ID, которые необходимы для вставки в таблицу rates.
        matchingRateList.stream().forEach((rate) -> {
            Currency currency = currencyList.stream().filter((item) ->
                    item.getCode().equals(rate.getCurrency().getCode())
            ).findFirst().get();
            rate.setCurrency(currency);
        });

        //Задаем время изменения курса и добавляем новые курсы валют в БД
        Timestamp time = new Timestamp(System.currentTimeMillis());
        matchingRateList.stream().forEach((rate) -> rate.setDate(time));
        rateDao.addAll(matchingRateList);
    }

    @Override
    public List<RateViewDto> getAll() {
        List<Rate> rateList = rateDao.getAll();
        return getViewDtos(rateList);
    }

    @Override
    public RateViewDto getActualByCurrencyId(int currencyId) {
        Rate rate = rateDao.getActualByCurrencyId(currencyId);
        return conversionService.convert(rate, RateViewDto.class);
    }

    @Override
    public List<RateViewDto> getActual() {
        List<Rate> rateList = rateDao.getActualRates();
        return getViewDtos(rateList);
    }

    private List<RateViewDto> getViewDtos(List<Rate> rateList) {
        return rateList.stream()
                .map((rate)->conversionService.convert(rate, RateViewDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Метод возвращает список валют, которые имеются как в базе данных,
     * так и в списке List<Rate> rateList, переданном в качестве аргумента.
     */
    private List<Currency> getMatchingCurrencies(List<Rate> rateList) {
        List<String> currencyCodes = rateList.stream()
                .map(rate -> rate.getCurrency().getCode())
                .collect(Collectors.toList());
        return currencyDao.getAllByCodes(currencyCodes);
    }
}
