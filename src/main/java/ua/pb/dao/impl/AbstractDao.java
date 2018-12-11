package ua.pb.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Properties;

/**
 * Класс AbstractDao содержит поля, общие для всех классов DAO-слоя
 * (кроме класса UserDaoImpl, реализованном на "чистом" JDBC)
 * Автор: Бардась А. А.
 */
public class AbstractDao {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("messageProperties")
    protected Properties properties;
}
