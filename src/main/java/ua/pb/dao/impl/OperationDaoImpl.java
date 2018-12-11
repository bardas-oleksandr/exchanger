package ua.pb.dao.impl;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import ua.pb.dao.OperationDao;
import ua.pb.exception.ApplicationException;
import ua.pb.logger.LoggerTemplate;
import ua.pb.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Класс OperationDaoImpl содержит методы доступа к информации
 * об объектах класса Operation, храняшейся в БД
 * Автор: Бардась А. А.
 */
@Repository("operationDao")
public class OperationDaoImpl extends AbstractDao implements OperationDao, LoggerTemplate {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void create(@NonNull Operation operation) {
        final String sql = "INSERT INTO operations " +
                "(operation_rate_id, operation_nbu_rate_id, operation_user_id, operation_buy, " +
                "operation_sum_hrn, operation_sum_curr, operation_time, operation_deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setInt(1, operation.getRate().getId());
                    ps.setInt(2, operation.getNbuRate().getId());
                    ps.setInt(3, operation.getUser().getId());
                    ps.setBoolean(4, operation.isBuyOperation());
                    ps.setFloat(5, operation.getSumHrn());
                    ps.setFloat(6, operation.getSumCurrency());
                    ps.setTimestamp(7, operation.getDate());
                    ps.setBoolean(8, operation.isDeleted());
                    return ps;
                }
            }, keyHolder);
            operation.setId(keyHolder.getKey().intValue());
        } catch (DataIntegrityViolationException | NullPointerException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_OPERATION"), e);
        }
    }

    @Override
    public void update(@NonNull Operation operation) {
        final String sql = "UPDATE operations " +
                "SET operation_rate_id=?, operation_nbu_rate_id=?, operation_user_id=?, " +
                "operation_buy=?, operation_sum_hrn=?, operation_sum_curr=?, operation_time=?, " +
                "operation_deleted=? " +
                "WHERE id=?";
        try {
            if (jdbcTemplate.update(sql, operation.getRate().getId(), operation.getNbuRate().getId()
                    , operation.getUser().getId(), operation.isBuyOperation(), operation.getSumHrn()
                    , operation.getSumCurrency(), operation.getDate(), operation.isDeleted()
                    , operation.getId()) == 0) {
                String message = properties.getProperty("FAILED_UPDATE_OPERATION_NONEXISTENT");
                logErrorMessage(message);
                throw new ApplicationException(message);
            }
        } catch (DataIntegrityViolationException | NullPointerException e) {
            logError(e);
            throw new ApplicationException(properties
                    .getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"), e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_OPERATION"), e);
        }
    }

    @Override
    public void updateDeletedColumn(boolean deleted, int operationId) {
        final String sql = "UPDATE operations SET operation_deleted=? WHERE id=?";
        try {
            if (jdbcTemplate.update(sql, deleted, operationId) == 0) {
                String message = properties.getProperty("FAILED_UPDATE_OPERATION_NONEXISTENT");
                logErrorMessage(message);
                throw new ApplicationException(message);
            }
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_OPERATION"), e);
        }
    }

    @Override
    public Operation getById(int id) {
        final String sql = "SELECT * FROM operations op " +
                "INNER JOIN rates ON op.operation_rate_id=rates.id " +
                "INNER JOIN currencies curr ON rates.rate_currency_id=curr.id " +
                "INNER JOIN nbu_rates nbu ON op.operation_nbu_rate_id=nbu.id " +
                "INNER JOIN users ON op.operation_user_id=users.id " +
                "WHERE op.id=?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}
                    , new OperationRowMapper());
        } catch (EmptyResultDataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("EMPTY_RESULTSET")
                    + Operation.class, e);
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_OPERATION"), e);
        }
    }

    @Override
    public List<Operation> getAll() {
        final String sql = "SELECT * FROM operations op " +
                "INNER JOIN rates ON op.operation_rate_id=rates.id " +
                "INNER JOIN currencies curr ON rates.rate_currency_id=curr.id " +
                "INNER JOIN nbu_rates nbu ON op.operation_nbu_rate_id=nbu.id " +
                "INNER JOIN users ON op.operation_user_id=users.id";
        try {
            return jdbcTemplate.query(sql, new OperationRowMapper());
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_OPERATION"), e);
        }
    }

    @Override
    public List<Operation> getAllNotDeletedByUserId(int userId) {
        final String sql = "SELECT * FROM operations op " +
                "INNER JOIN rates ON op.operation_rate_id=rates.id " +
                "INNER JOIN currencies curr ON rates.rate_currency_id=curr.id " +
                "INNER JOIN nbu_rates nbu ON op.operation_nbu_rate_id=nbu.id " +
                "INNER JOIN users ON op.operation_user_id=users.id " +
                "WHERE op.operation_user_id=? " +
                "AND op.operation_deleted='false'";
        try {
            return jdbcTemplate.query(sql, new Object[]{userId}, new OperationRowMapper());
        } catch (DataAccessException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_OPERATION"), e);
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private class OperationRowMapper implements RowMapper<Operation> {

        @Nullable
        @Override
        public Operation mapRow(ResultSet rs, int rowNum) throws SQLException {
            Operation operation = new Operation();
            operation.setId(rs.getInt(1));
            operation.setBuyOperation(rs.getBoolean(5));
            operation.setSumHrn(rs.getFloat(6));
            operation.setSumCurrency(rs.getFloat(7));
            operation.setDate(rs.getTimestamp(8));
            operation.setDeleted(rs.getBoolean(9));

            Rate rate = new Rate();
            rate.setId(rs.getInt(10));
            rate.setDate(rs.getTimestamp(12));
            rate.setSale(rs.getFloat(13));
            rate.setBuy(rs.getFloat(14));
            Currency currency = new Currency();
            currency.setId(rs.getInt(15));
            currency.setCode(rs.getString(16));
            currency.setName(rs.getString(17));
            rate.setCurrency(currency);
            operation.setRate(rate);

            NbuRate nbuRate = new NbuRate();
            nbuRate.setId(rs.getInt(18));
            nbuRate.setCurrency(currency);
            nbuRate.setPrice(rs.getFloat(20));
            nbuRate.setDate(rs.getTimestamp(21));
            operation.setNbuRate(nbuRate);

            User user = new User();
            user.setId(rs.getInt(22));
            user.setUsername(rs.getString(23));
            user.setPassword(rs.getString(24));
            user.setState(User.State.get(rs.getInt(25)));
            operation.setUser(user);
            return operation;
        }
    }
}
