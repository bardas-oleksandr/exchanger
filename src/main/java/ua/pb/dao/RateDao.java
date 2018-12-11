package ua.pb.dao;

import lombok.NonNull;
import ua.pb.model.Rate;

import java.util.List;

public interface RateDao {
    void create(@NonNull Rate rate);

    void update(@NonNull Rate rate);

    void delete(int id);

    Rate getById(int id);

    void addAll(@NonNull List<Rate> rateList);

    List<Rate> getAll();

    Rate getActualByCurrencyId(int currencyId);

    List<Rate> getActualRates();
}
