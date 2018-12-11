package ua.pb.service;

import lombok.NonNull;
import ua.pb.dto.create.CurrencyCreateDto;
import ua.pb.dto.view.CurrencyViewDto;

import java.util.List;

public interface CurrencyService {

    CurrencyViewDto create(@NonNull CurrencyCreateDto currencyCreateDto);

    CurrencyViewDto update(@NonNull CurrencyCreateDto currencyCreateDto, int currencyId);

    void delete(int currencyId);

    CurrencyViewDto getById(int currencyId);

    List<CurrencyViewDto> getAll();
}
