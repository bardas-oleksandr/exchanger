package ua.pb.dao.impl;

import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ua.pb.dao.UserDao;
import ua.pb.exception.ApplicationException;
import ua.pb.logger.LoggerTemplate;
import ua.pb.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Класс OperationDaoImpl содержит методы доступа к информации
 * об объектах класса User, храняшейся в БД
 * Автор: Бардась А. А.
 */
@Repository("userDao")
public class UserDaoImpl implements UserDao, LoggerTemplate {

    private static final Logger logger = LogManager.getLogger();

    private final int PRIMARY_KEY_CONSTRAINT_VIOLATION = 23505;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    @Autowired
    private Connection connection;

    @Override
    public void create(@NonNull User user) {
        String sql = "INSERT INTO users (user_name, user_password, user_state) VALUES (?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"})) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setInt(3, user.getState().ordinal());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            logError(e);
            if (e.getErrorCode() == PRIMARY_KEY_CONSTRAINT_VIOLATION) {
                throw new ApplicationException(properties.getProperty("NOT_UNIQUE_USER"), e);
            }
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_USER"), e);
        }
    }

    @Override
    public void update(@NonNull User user) {
        String sql = "UPDATE users SET user_name = ?, user_state = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setInt(2, user.getState().ordinal());
            statement.setInt(3, user.getId());
            int count = statement.executeUpdate();
            if(count == 0){
                throw new ApplicationException(properties.getProperty("FAILED_UPDATE_USER_NONEXISTENT"));
            }
        } catch (SQLException e) {
            logError(e);
            if (e.getErrorCode() == PRIMARY_KEY_CONSTRAINT_VIOLATION) {
                throw new ApplicationException(properties.getProperty("NOT_UNIQUE_USER"), e);
            }
            throw new ApplicationException(properties.getProperty("FAILED_SAVE_USER"), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if(statement.executeUpdate() == 0){
                throw new ApplicationException(properties.getProperty("FAILED_DELETE_USER_NONEXISTENT"));
            }
        } catch (SQLException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_DELETE_USER"), e);
        }
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                String message = properties.getProperty("EMPTY_RESULTSET") + User.class;
                logErrorMessage(message);
                throw new ApplicationException(message);
            }
            return new UserRowMapper().mapRow(resultSet, 1);
        } catch (SQLException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_USER"), e);
        }
    }

    @Override
    public User getByUsername(@NonNull String username) {
        String sql = "SELECT * FROM users WHERE user_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(!resultSet.next()){
                String message = properties.getProperty("EMPTY_RESULTSET") + User.class;
                logErrorMessage(message);
                throw new ApplicationException(message);
            }
            return new UserRowMapper().mapRow(resultSet, 1);
        }catch (SQLException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_USER"), e);
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<User> userList = extractUserList(new UserRowMapper(), resultSet);
            if(userList.size() == 0){
                String message = properties.getProperty("EMPTY_RESULTSET") + User.class;
                logErrorMessage(message);
                throw new ApplicationException(message);
            }
            return userList;
        } catch (SQLException e) {
            logError(e);
            throw new ApplicationException(properties.getProperty("FAILED_GET_ALL_USERS"), e);
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private List<User> extractUserList(UserRowMapper mapper, ResultSet resultSet)
            throws SQLException {
        List<User> result = new ArrayList<>();
        int line = 1;
        while (resultSet.next()) {
            result.add(mapper.mapRow(resultSet, line++));
        }
        return result;
    }

    /**
     * Класс UserRowMapper предназначен для конвертации
     * результата запроса к БД в объект класса User
     * Автор: Бардась А. А.
     */
    private class UserRowMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user = new User();
            user.setId(resultSet.getInt("id"));
            user.setUsername(resultSet.getString("user_name"));
            user.setPassword(resultSet.getString("user_password"));
            user.setState(User.State.get(resultSet.getInt("user_state")));
            return user;
        }
    }
}
