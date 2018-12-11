package ua.pb.service.impl;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ua.pb.dao.CurrencyDao;
import ua.pb.dao.NbuRateDao;
import ua.pb.dto.create.NbuRateCreateDto;
import ua.pb.dto.view.NbuRateViewDto;
import ua.pb.model.Currency;
import ua.pb.model.NbuRate;
import ua.pb.service.NbuRateService;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service("nbuRateService")
public class NbuRateServiceImpl implements NbuRateService {

    @Autowired
    private NbuRateDao nbuRateDao;

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private ConversionService conversionService;

    @Override
    public NbuRateViewDto create(@NonNull NbuRateCreateDto nbuRateCreateDto) {
        NbuRate nbuRate = conversionService.convert(nbuRateCreateDto, NbuRate.class);
        nbuRate.setDate(new Timestamp(System.currentTimeMillis()));
        nbuRate.setCurrency(currencyDao.getByCode(nbuRate.getCurrency().getCode()));
        nbuRateDao.create(nbuRate);
        return conversionService.convert(nbuRate, NbuRateViewDto.class);
    }

    @Override
    public NbuRateViewDto update(@NonNull NbuRateCreateDto nbuRateCreateDto, int nbuRateId) {
        NbuRate nbuRate = conversionService.convert(nbuRateCreateDto, NbuRate.class);
        nbuRate.setId(nbuRateId);
        nbuRate.setDate(new Timestamp(System.currentTimeMillis()));
        nbuRate.setCurrency(currencyDao.getByCode(nbuRate.getCurrency().getCode()));
        nbuRateDao.update(nbuRate);
        return conversionService.convert(nbuRate, NbuRateViewDto.class);
    }

    @Override
    public void delete(int nbuRateId) {
        nbuRateDao.delete(nbuRateId);
    }

    @Override
    public NbuRateViewDto getById(int nbuRateId) {
        NbuRate nbuRate = nbuRateDao.getById(nbuRateId);
        return conversionService.convert(nbuRate, NbuRateViewDto.class);
    }

    /**
     * Метод выполняет добавление информации о новых курсах валют НБУ
     * только для тех видов валюты, которая уже имеется в базе данных
     */
    @Override
    public void addAllForExistingCurrencies(List<NbuRateCreateDto> nbuRateCreatewDtoList) {
        //Конвертация
        List<NbuRate> nbuRateList = nbuRateCreatewDtoList.stream()
                .map((nbuRateCreateDto) -> conversionService
                        .convert(nbuRateCreateDto, NbuRate.class))
                .collect(Collectors.toList());

        //Список валют, которые есть как в базе данных, так и в списке rateList
        List<Currency> currencyList = getMatchingCurrencies(nbuRateList);

        //Из списка nbuRateList получаем новый список объектов NbuRate,
        //валюта которых есть в базе данных.
        List<NbuRate> matchingNbuRateList = nbuRateList.stream()
                .filter((nbuRate) -> currencyList.stream()
                        .anyMatch((currency -> currency.getCode()
                                .equals(nbuRate.getCurrency().getCode()))))
                .collect(Collectors.toList());

        //Обновляем поля Currency для объектов из списка nbuRateList.
        //После обновления нам становятся доступны не только значения
        //кодов валют, но и их ID, которые необходимы для вставки в таблицу nbu_rates.
        matchingNbuRateList.stream().forEach((nbuRate) -> {
            Currency currency = currencyList.stream().filter((item) ->
                    item.getCode().equals(nbuRate.getCurrency().getCode())
            ).findFirst().get();
            nbuRate.setCurrency(currency);
        });

        //Задаем время изменения курса и добавляем новые курсы валют в БД
        Timestamp time = new Timestamp(System.currentTimeMillis());
        matchingNbuRateList.stream().forEach((nbuRate) -> nbuRate.setDate(time));
        nbuRateDao.addAll(matchingNbuRateList);
    }

    @Override
    public List<NbuRateViewDto> getAll() {
        List<NbuRate> nbuRateList = nbuRateDao.getAll();
        return getViewDtos(nbuRateList);
    }

    @Override
    public NbuRateViewDto getActualByCurrencyId(int currencyId) {
        NbuRate nbuRate = nbuRateDao.getActualByCurrencyId(currencyId);
        return conversionService.convert(nbuRate, NbuRateViewDto.class);
    }

    @Override
    public List<NbuRateViewDto> getActual() {
        List<NbuRate> nbuRateList = nbuRateDao.getActualRates();
        return getViewDtos(nbuRateList);
    }

    private List<NbuRateViewDto> getViewDtos(List<NbuRate> nbuRateList) {
        return nbuRateList.stream()
                .map((nbuRate) -> conversionService
                        .convert(nbuRate, NbuRateViewDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Метод возвращает список валют, которые имеются как в базе данных,
     * так и в списке List<NbuRate> nbuRateList, переданном в качестве аргумента.
     */
    private List<Currency> getMatchingCurrencies(List<NbuRate> nbuRateList) {
        List<String> currencyCodes = nbuRateList.stream()
                .map(nbuRate -> nbuRate.getCurrency().getCode())
                .collect(Collectors.toList());
        return currencyDao.getAllByCodes(currencyCodes);
    }
}
