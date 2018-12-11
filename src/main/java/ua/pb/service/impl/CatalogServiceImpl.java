package ua.pb.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import ua.pb.dao.NbuRateDao;
import ua.pb.dao.RateDao;
import ua.pb.dto.view.CatalogItemViewDto;
import ua.pb.dto.view.NbuRateViewDto;
import ua.pb.dto.view.RateViewDto;
import ua.pb.model.NbuRate;
import ua.pb.model.Rate;
import ua.pb.service.CatalogService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("catalogService")
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private NbuRateDao nbuRateDao;

    @Autowired
    private RateDao rateDao;

    @Autowired
    private ConversionService conversionService;

    @Override
    public CatalogItemViewDto getCatalogItem(int currencyId) {
        NbuRate nbuRate = nbuRateDao.getActualByCurrencyId(currencyId);
        Rate rate = rateDao.getActualByCurrencyId(currencyId);

        NbuRateViewDto nbuRateViewDto = conversionService.convert(nbuRate, NbuRateViewDto.class);
        RateViewDto rateViewDto = conversionService.convert(rate, RateViewDto.class);

        CatalogItemViewDto catalogItemViewDto = new CatalogItemViewDto();
        catalogItemViewDto.setNbuRateViewDto(nbuRateViewDto);
        catalogItemViewDto.setRateViewDto(rateViewDto);
        return catalogItemViewDto;
    }

    /**Метод извлекает из базы данных все текущие курсы Приватбанка и НБУ,
     * формирует из двух списков List<NbuRate> и List<Rate>
     * один список List<CatalogItemViewDto>
     */
    @Override
    public List<CatalogItemViewDto> getCatalog() {
        List<NbuRate> nbuRates = nbuRateDao.getActualRates();
        List<Rate> rates = rateDao.getActualRates();

        List<NbuRateViewDto> nbuRateViewDtoList = nbuRates.stream()
                .map((nbuRate)->conversionService
                        .convert(nbuRate, NbuRateViewDto.class))
                .collect(Collectors.toList());
        List<RateViewDto> rateViewDtoList = rates.stream()
                .map((rate)->conversionService
                        .convert(rate, RateViewDto.class))
                .collect(Collectors.toList());

        List<CatalogItemViewDto> catalogItemViewDtos = new ArrayList<>();
        rateViewDtoList.stream().forEach((rate)->catalogItemViewDtos
                .add(new CatalogItemViewDto(rate)));

        //Для каждого курса валюты Приватбанка в справочнике находим
        //соответсвующий курс НБУ (если он есть)
        catalogItemViewDtos.stream().forEach((item)->{
            nbuRateViewDtoList.stream().forEach((nbuRate)->{
                String currencyCode = item.getRateViewDto().getCurrencyViewDto().getCode();
                String nbuCurrencyCode = nbuRate.getCurrencyViewDto().getCode();
                if(nbuCurrencyCode.equals(currencyCode)){
                    item.setNbuRateViewDto(nbuRate);
                }
            });
        });

        return catalogItemViewDtos;
    }
}
