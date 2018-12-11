package ua.pb.dao;

import lombok.NonNull;
import ua.pb.model.NbuRate;

import java.util.List;

public interface NbuRateDao {
    void create(@NonNull NbuRate nbuRate);

    void update(@NonNull NbuRate nbuRate);

    void delete(int id);

    NbuRate getById(int id);

    void addAll(@NonNull List<NbuRate> nbuRateList);

    List<NbuRate> getAll();

    NbuRate getActualByCurrencyId(int currencyId);

    List<NbuRate> getActualRates();
}
