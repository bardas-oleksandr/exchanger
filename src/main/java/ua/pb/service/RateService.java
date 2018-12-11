package ua.pb.service;

import lombok.NonNull;
import ua.pb.dto.create.RateCreateDto;
import ua.pb.dto.view.RateViewDto;

import java.util.List;

public interface RateService {

    RateViewDto create(@NonNull RateCreateDto rateCreateDto);

    RateViewDto update(@NonNull RateCreateDto rateCreateDto, int rateId);

    void delete(int rateId);

    RateViewDto getById(int rateId);

    void addAllForExistingCurrencies(List<RateCreateDto> rateCreateDtoList);

    List<RateViewDto> getAll();

    RateViewDto getActualByCurrencyId(int currencyId);

    List<RateViewDto> getActual();
}
