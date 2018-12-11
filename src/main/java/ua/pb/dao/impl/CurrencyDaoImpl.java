package ua.pb.dao.impl;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ua.pb.dao.CurrencyDao;
import ua.pb.exception.ApplicationException;
import ua.pb.logger.LoggerTemplate;
import ua.pb.model.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс CurrencyDaoImpl содержит методы доступа к информации
 * об объектах класса Currency, храняшейся в БД
 * Автор: Бардась А. А.
 */
@Repository("currencyDao")
public class CurrencyDaoImpl extends AbstractDao implements CurrencyDao, LoggerTemplate {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void create(@NonNull Currency currency) {
        final String sql = "INSERT INTO currencies (currency_code, currency_name) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setString(1, currency.getCode());
                    ps.setString(2, currency.getName());
                    return ps;
                }
            }, keyHolder);
            currency.setId(keyHolder.getKey().intValue());
        } catch (DuplicateKeyException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("NOT_UNIQUE_CURRENCY"), e);
        } catch (DataIntegrityViolationException | NullPointerException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("DATA_INTEGRITY_VIOLATION_FOR_CURRENCY"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_CURRENCY"), e);
        }
    }

    @Override
    public void update(@NonNull Currency currency) {
        final String sql = "UPDATE currencies SET currency_code=?, currency_name=? WHERE id=?";
        try {
            if (jdbcTemplate.update(sql, currency.getCode(), currency.getName()
                    , currency.getId()) == 0) {
                String message = properties.getProperty("FAILED_UPDATE_CURRENCY_NONEXISTENT");
                logErrorMessage(message);
                throw new ApplicationException(message);
            }
        } catch (DuplicateKeyException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("NOT_UNIQUE_CURRENCY"), e);
        } catch (DataIntegrityViolationException | NullPointerException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("DATA_INTEGRITY_VIOLATION_FOR_CURRENCY"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_CURRENCY"), e);
        }
    }

    @Override
    public void delete(int id) {
        final String sql = "DELETE FROM currencies WHERE id=?";
        try {
            if (jdbcTemplate.update(sql, id) == 0) {
                throw new ApplicationException(properties
                        .getProperty("FAILED_UPDATE_CURRENCY_NONEXISTENT"));
            }
        } catch (DataIntegrityViolationException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("INTEGRITY_VIOLATION_WHILE_DELETE_CURRENCY"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_DELETE_CURRENCY"), e);
        }
    }

    @Override
    public Currency getById(int id) {
        final String sql = "SELECT * FROM currencies WHERE id=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}
                    , new CurrencyRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("EMPTY_RESULTSET")
                    + Currency.class, e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_CURRENCY"), e);
        }
    }

    @Override
    public Currency getByCode(String code) {
        final String sql = "SELECT * FROM currencies WHERE currencies.currency_code=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{code}
                    , new CurrencyRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("EMPTY_RESULTSET")
                    + Currency.class, e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_CURRENCY"), e);
        }
    }

    @Override
    public List<Currency> getAll() {
        final String sql = "SELECT * FROM currencies";
        try {
            return jdbcTemplate.query(sql, new CurrencyRowMapper());
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_CURRENCY"), e);
        }
    }

    @Override
    public List<Currency> getAllByCodes(List<String> currencyCodes) {
        try {
            List<Currency> currencyList = new ArrayList<>();
            for (String code: currencyCodes) {
                try{
                    currencyList.add(getByCode(code));
                }catch (ApplicationException e){
                    if(!e.getMessage().equals(properties.getProperty("EMPTY_RESULTSET")
                            + Currency.class)){
                        throw new ApplicationException(e.getMessage());
                    }
                }
            }
            return currencyList;
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_CURRENCY"), e);
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private class CurrencyRowMapper implements RowMapper<Currency> {

        @Nullable
        @Override
        public Currency mapRow(ResultSet rs, int rowNum) throws SQLException {
            Currency currency = new Currency();
            currency.setId(rs.getInt(1));
            currency.setCode(rs.getString(2));
            currency.setName(rs.getString(3));
            return currency;
        }
    }
}
