package ua.pb.service;

import lombok.NonNull;
import ua.pb.dto.create.NbuRateCreateDto;
import ua.pb.dto.view.NbuRateViewDto;

import java.util.List;

public interface NbuRateService {

    NbuRateViewDto create(@NonNull NbuRateCreateDto nbuRateCreateDto);

    NbuRateViewDto update(@NonNull NbuRateCreateDto nbuRateCreateDto, int nbuRateId);

    void delete(int nbuRateId);

    NbuRateViewDto getById(int nbuRateId);

    void addAllForExistingCurrencies(List<NbuRateCreateDto> nbuRateCreatewDtoList);

    List<NbuRateViewDto> getAll();

    NbuRateViewDto getActualByCurrencyId(int currencyId);

    List<NbuRateViewDto> getActual();
}
