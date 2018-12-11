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
import ua.pb.dao.RateDao;
import ua.pb.exception.ApplicationException;
import ua.pb.logger.LoggerTemplate;
import ua.pb.model.Currency;
import ua.pb.model.Rate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Класс OperationDaoImpl содержит методы доступа к информации
 * об объектах класса Rate (курс покупки-продажи приватбанка), храняшейся в БД
 * Автор: Бардась А. А.
 */
@Repository("rateDao")
public class RateDaoImpl extends AbstractDao implements RateDao, LoggerTemplate {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void create(@NonNull Rate rate) {
        final String sql = "INSERT INTO rates " +
                "(rate_currency_id, rate_date, rate_sale, rate_buy) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setInt(1, rate.getCurrency().getId());
                    ps.setTimestamp(2, rate.getDate());
                    ps.setFloat(3, rate.getSale());
                    ps.setFloat(4, rate.getBuy());
                    return ps;
                }
            }, keyHolder);
            rate.setId(keyHolder.getKey().intValue());
        } catch (DataIntegrityViolationException | NullPointerException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_RATE"), e);
        }
    }

    @Override
    public void update(@NonNull Rate rate) {
        final String sql = "UPDATE rates " +
                "SET rate_currency_id=?, rate_date=?, rate_sale=?, rate_buy=? " +
                "WHERE id=?";
        try {
            if (jdbcTemplate.update(sql, rate.getCurrency().getId(), rate.getDate()
                    , rate.getSale(), rate.getBuy(), rate.getId()) == 0) {
                String message = properties.getProperty("FAILED_UPDATE_RATE_NONEXISTENT");
                logErrorMessage(message);
                throw new ApplicationException(message);
            }
        } catch (DataIntegrityViolationException | NullPointerException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_RATE"), e);
        }
    }

    @Override
    public void delete(int id) {
        final String sql = "DELETE FROM rates WHERE id=?";
        try {
            if (jdbcTemplate.update(sql, id) == 0) {
                throw new ApplicationException(properties
                        .getProperty("FAILED_UPDATE_RATE_NONEXISTENT"));
            }
        } catch (DataIntegrityViolationException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("INTEGRITY_VIOLATION_WHILE_DELETE_RATE"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_DELETE_RATE"), e);
        }
    }

    @Override
    public Rate getById(int id) {
        final String sql = "SELECT * FROM rates " +
                "INNER JOIN currencies ON rates.rate_currency_id=currencies.id " +
                "WHERE rates.id=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}
                    , new RateRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("EMPTY_RESULTSET")
                    + Rate.class, e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_RATE"), e);
        }
    }

    @Override
    public void addAll(List<Rate> rateList) {
        final String sql = "INSERT INTO rates " +
                "(rate_currency_id, rate_date, rate_sale, rate_buy) " +
                "VALUES (?, ?, ?, ?)";

        try {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Rate rate = rateList.get(i);
                    ps.setInt(1, rate.getCurrency().getId());
                    ps.setTimestamp(2, rate.getDate());
                    ps.setFloat(3, rate.getSale());
                    ps.setFloat(4, rate.getBuy());
                }

                @Override
                public int getBatchSize() {
                    return rateList.size();
                }
            });
        } catch (DataIntegrityViolationException | NullPointerException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_RATE"), e);
        }
    }

    @Override
    public List<Rate> getAll() {
        final String sql = "SELECT * FROM rates " +
                "INNER JOIN currencies ON rates.rate_currency_id=currencies.id";
        try {
            return jdbcTemplate.query(sql, new RateRowMapper());
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_RATE"), e);
        }
    }

    @Override
    public Rate getActualByCurrencyId(int currencyId) {
        //Запрос с MAX и GROUP BY будет работать только в MySQL
//        final String sql = "SELECT r.id, r.rate_currency_id, r.rate_date, " +
//                "r.rate_sale, r.rate_buy, " +
//                "c.id, c.currency_code, c.currency_name " +
//                "FROM rates r " +
//                "INNER JOIN currencies c ON r.rate_currency_id=c.id " +
//                "WHERE r.rate_currency_id = ? " +
//                "AND r.rate_date IN (SELECT MAX(r1.rate_date) FROM rates r1 " +
//                "                      WHERE r1.rate_currency_id = ?)";

        final String sql = "SELECT r.*, c.*, r1.rate_currency_id, r1.rate_date FROM rates r " +
                "INNER JOIN currencies c ON r.rate_currency_id=c.id " +
                "LEFT JOIN rates r1 ON r.rate_currency_id=r1.rate_currency_id AND " +
                "r.rate_date < r1.rate_date " +
                "WHERE r1.rate_date is NULL " +
                "AND r.rate_currency_id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{currencyId}
                    , new RateRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("EMPTY_RESULTSET")
                    + Rate.class, e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_RATE"), e);
        }
    }

    @Override
    public List<Rate> getActualRates() {
//        final String sql = "SELECT r.*, c.*, r1.rate_currency_id, r1.rate_date FROM rates r " +
//                "INNER JOIN currencies c ON r.rate_currency_id=c.id " +
//                "LEFT JOIN rates r1 ON r.rate_currency_id=r1.rate_currency_id AND " +
//                "r.rate_date < r1.rate_date " +
//                "WHERE r1.rate_date is NULL";

        final String sql = "SELECT r.*, c.* FROM rates r " +
                "INNER JOIN currencies c ON r.rate_currency_id=c.id " +
                "INNER JOIN (SELECT rate_currency_id, MAX(rate_date) AS max_date " +
                "FROM rates GROUP BY rate_currency_id) AS r1 " +
                "ON r.rate_currency_id=r1.rate_currency_id AND r.rate_date=r1.max_date " +
                "ORDER BY c.currency_name";


        try {
            return jdbcTemplate.query(sql, new RateRowMapper());
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_RATE"), e);
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private class RateRowMapper implements RowMapper<Rate> {

        @Nullable
        @Override
        public Rate mapRow(ResultSet rs, int rowNum) throws SQLException {
            Rate rate = new Rate();
            rate.setId(rs.getInt(1));
            rate.setDate(rs.getTimestamp(3));
            rate.setSale(rs.getFloat(4));
            rate.setBuy(rs.getFloat(5));
            Currency currency = new Currency();
            currency.setId(rs.getInt(6));
            currency.setCode(rs.getString(7));
            currency.setName(rs.getString(8));
            rate.setCurrency(currency);
            return rate;
        }
    }
}
