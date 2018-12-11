package ua.pb.dao;

import lombok.NonNull;
import ua.pb.model.Currency;
import ua.pb.model.NbuRate;

import java.util.List;

public interface CurrencyDao {
    void create(@NonNull Currency currency);

    void update(@NonNull Currency currency);

    void delete(int id);

    Currency getById(int id);

    Currency getByCode(String code);

    List<Currency> getAll();

    List<Currency> getAllByCodes(List<String> currencyCodes);
}
