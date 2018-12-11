package ua.pb.dao.impl;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ua.pb.dao.NbuRateDao;
import ua.pb.exception.ApplicationException;
import ua.pb.logger.LoggerTemplate;
import ua.pb.model.Currency;
import ua.pb.model.NbuRate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Класс NbuRateDaoImpl содержит методы доступа к информации
 * об объектах класса NbuRate (курс НБУ), храняшейся в БД
 * Автор: Бардась А. А.
 */
@Repository("nbuRateDao")
public class NbuRateDaoImpl extends AbstractDao implements NbuRateDao, LoggerTemplate {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void create(@NonNull NbuRate nbuRate) {
        final String sql = "INSERT INTO nbu_rates " +
                "(nbu_rate_currency_id, nbu_rate, nbu_rate_date) " +
                "VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setInt(1, nbuRate.getCurrency().getId());
                    ps.setFloat(2, nbuRate.getPrice());
                    ps.setTimestamp(3, nbuRate.getDate());
                    return ps;
                }
            }, keyHolder);
            nbuRate.setId(keyHolder.getKey().intValue());
        } catch (DataIntegrityViolationException | NullPointerException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("DATA_INTEGRITY_VIOLATION_FOR_NBU_RATE"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_NBU_RATE"), e);
        }
    }

    @Override
    public void update(@NonNull NbuRate nbuRate) {
        final String sql = "UPDATE nbu_rates " +
                "SET nbu_rate_currency_id=?, nbu_rate=?, nbu_rate_date=? " +
                "WHERE id=?";
        try {
            if (jdbcTemplate.update(sql, nbuRate.getCurrency().getId(), nbuRate.getPrice()
                    , nbuRate.getDate(), nbuRate.getId()) == 0) {
                String message = properties.getProperty("FAILED_UPDATE_NBU_RATE_NONEXISTENT");
                logErrorMessage(message);
                throw new ApplicationException(message);
            }
        } catch (DataIntegrityViolationException | NullPointerException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("DATA_INTEGRITY_VIOLATION_FOR_NBU_RATE"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_NBU_RATE"), e);
        }
    }

    @Override
    public void delete(int id) {
        final String sql = "DELETE FROM nbu_rates WHERE id=?";
        try {
            if (jdbcTemplate.update(sql, id) == 0) {
                throw new ApplicationException(properties
                        .getProperty("FAILED_UPDATE_NBU_RATE_NONEXISTENT"));
            }
        } catch (DataIntegrityViolationException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("INTEGRITY_VIOLATION_WHILE_DELETE_NBU_RATE"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_DELETE_NBU_RATE"), e);
        }
    }

    @Override
    public NbuRate getById(int id) {
        final String sql = "SELECT * FROM nbu_rates " +
                "INNER JOIN currencies ON nbu_rates.nbu_rate_currency_id=currencies.id " +
                "WHERE nbu_rates.id=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}
                    , new NbuRateRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("EMPTY_RESULTSET")
                    + NbuRate.class, e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_NBU_RATE"), e);
        }
    }

    @Override
    public void addAll(List<NbuRate> nbuRateList) {
        final String sql = "INSERT INTO nbu_rates " +
                "(nbu_rate_currency_id, nbu_rate, nbu_rate_date) " +
                "VALUES (?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    NbuRate nbuRate = nbuRateList.get(i);
                    ps.setInt(1, nbuRate.getCurrency().getId());
                    ps.setFloat(2, nbuRate.getPrice());
                    ps.setTimestamp(3, nbuRate.getDate());
                }

                @Override
                public int getBatchSize() {
                    return nbuRateList.size();
                }
            });
        } catch (DataIntegrityViolationException | NullPointerException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("DATA_INTEGRITY_VIOLATION_FOR_NBU_RATE"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_NBU_RATE"), e);
        }
    }

    @Override
    public List<NbuRate> getAll() {
        final String sql = "SELECT * FROM nbu_rates " +
                "INNER JOIN currencies ON nbu_rates.nbu_rate_currency_id=currencies.id";
        try {
            return jdbcTemplate.query(sql, new NbuRateRowMapper());
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_NBU_RATE"), e);
        }
    }

    @Override
    public NbuRate getActualByCurrencyId(int currencyId) {
        final String sql = "SELECT n.*, c.*, n1.nbu_rate_currency_id, n1.nbu_rate_date FROM nbu_rates n " +
                "INNER JOIN currencies c ON n.nbu_rate_currency_id=c.id " +
                "LEFT JOIN nbu_rates n1 ON n.nbu_rate_currency_id=n1.nbu_rate_currency_id AND " +
                "n.nbu_rate_date < n1.nbu_rate_date " +
                "WHERE n1.nbu_rate_date is NULL " +
                "AND n.nbu_rate_currency_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{currencyId}
                    , new NbuRateRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("EMPTY_RESULTSET")
                    + NbuRate.class, e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_RATE"), e);
        }
    }

    @Override
    public List<NbuRate> getActualRates() {
        final String sql = "SELECT n.*, c.*, n1.nbu_rate_currency_id, n1.nbu_rate_date FROM nbu_rates n " +
                "INNER JOIN currencies c ON n.nbu_rate_currency_id=c.id " +
                "LEFT JOIN nbu_rates n1 ON n.nbu_rate_currency_id=n1.nbu_rate_currency_id AND " +
                "n.nbu_rate_date < n1.nbu_rate_date " +
                "WHERE n1.nbu_rate_date is NULL";

        try {
            return jdbcTemplate.query(sql, new NbuRateRowMapper());
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_NBU_RATE"), e);
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private class NbuRateRowMapper implements RowMapper<NbuRate> {

        @Nullable
        @Override
        public NbuRate mapRow(ResultSet rs, int rowNum) throws SQLException {
            NbuRate nbuRate = new NbuRate();
            nbuRate.setId(rs.getInt(1));
            nbuRate.setPrice(rs.getFloat(3));
            nbuRate.setDate(rs.getTimestamp(4));
            Currency currency = new Currency();
            currency.setId(rs.getInt(5));
            currency.setCode(rs.getString(6));
            currency.setName(rs.getString(7));
            nbuRate.setCurrency(currency);
            return nbuRate;
        }
    }
}
