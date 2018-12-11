package ua.pb.dao.impl;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ua.pb.dao.CurrencyDao;
import ua.pb.dao.NbuRateDao;
import ua.pb.exception.ApplicationException;
import ua.pb.model.Currency;
import ua.pb.model.NbuRate;
import ua.pb.testconfig.TestContextConfig;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/*Класс CurrencyDaoImplTest содержит интеграционные тесты для проверки
* корректности работы методов доступа к данным, относящимся к сущности
* "Валюта"
*
* Автор: Бардась А.А.
* */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContextConfig.class})
@ActiveProfiles("test")
public class CurrencyDaoImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private NbuRateDao nbuRateDao;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    /*Сценарий: - добавление в базу данных информации о валюте;
    *           - все поля корректны, валюта уникальна
    * Результат: валюта успешно добавлена в базу данных.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void createTest_whenCurrencyIsUnique_thenOk() throws Exception {
        //GIVEN
        Currency currency = new Currency("PLZ", "Polski zloty");
        //WHEN
        currencyDao.create(currency);
        //THEN
        Currency extracted = currencyDao.getById(currency.getId());
        Assert.assertNotNull(extracted);
        Assert.assertEquals(currency, extracted);
    }

    /*Сценарий: - добавление в базу данных информации о валюте;
    *           - код валюты не уникален
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void createTest_whenCurrencyCodeIsNotUnique_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "Polski zloty");
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("NOT_UNIQUE_CURRENCY"));
        //WHEN-THEN
        currencyDao.create(currency);
    }

    /*Сценарий: - добавление в базу данных информации о валюте;
    *           - название валюты не уникально
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void createTest_whenCurrencyNameIsNotUnique_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("PLZ", "American dollar");
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("NOT_UNIQUE_CURRENCY"));
        //WHEN-THEN
        currencyDao.create(currency);
    }

    /*Сценарий: - добавление в базу данных информации о валюте;
    *           - код валюты равен null
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void createTest_whenCurrencyCodeEqualsNull_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency(null, "Polski zloty");
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_CURRENCY"));
        //WHEN-THEN
        currencyDao.create(currency);
    }

    /*Сценарий: - добавление в базу данных информации о валюте;
    *           - название валюты равно null
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void createTest_whenCurrencyNameEqualsNull_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("PLZ", null);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_CURRENCY"));
        //WHEN-THEN
        currencyDao.create(currency);
    }

    /*Сценарий: - добавление в базу данных информации о валюте;
    *           - валюта равна null
    * Результат: исключение NullPointerException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void createTest_whenCurrencyEqualsNull_thenException() throws Exception {
        //GIVEN
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("currency is marked @NonNull but is null");
        //WHEN-THEN
        currencyDao.create(null);
    }

    /*Сценарий: - изменение в базе данных информации о валюте;
    *           - все новые значения полей корректны, валюта уникальна
    * Результат: валюта успешно изменена в базе данных.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void updateTest_whenCurrencyIsUnique_thenOk() throws Exception {
        //GIVEN
        Currency currency = new Currency("PLZ", "Polski zloty");
        currency.setId(1);
        //WHEN
        currencyDao.update(currency);
        //THEN
        Currency extracted = currencyDao.getById(currency.getId());
        Assert.assertNotNull(extracted);
        Assert.assertEquals(currency, extracted);
    }

    /*Сценарий: - изменение в базе данных информации о валюте;
    *           - новый код валюты не уникален
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void updateTest_whenCurrencyCodeIsNotUnique_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("UAH", "Polski zloty");
        currency.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("NOT_UNIQUE_CURRENCY"));
        //WHEN-THEN
        currencyDao.update(currency);
    }

    /*Сценарий: - изменение в базе данных информации о валюте;
    *           - новое название валюты не уникально
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void updateTest_whenCurrencyNameIsNotUnique_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("PLZ", "Ukrainian hryvnia");
        currency.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("NOT_UNIQUE_CURRENCY"));
        //WHEN-THEN
        currencyDao.update(currency);
    }

    /*Сценарий: - изменение в базе данных информации о валюте;
    *           - новый код валюты равен null
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void updateTest_whenCurrencyCodeEqualsNull_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency(null, "Polski zloty");
        currency.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_CURRENCY"));
        //WHEN-THEN
        currencyDao.update(currency);
    }

    /*Сценарий: - изменение в базе данных информации о валюте;
    *           - новое название валюты равно null
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void updateTest_whenCurrencyNameEqualsNull_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("PLZ", null);
        currency.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_CURRENCY"));
        //WHEN-THEN
        currencyDao.update(currency);
    }

    /*Сценарий: - изменение в базе данных информации о валюте;
    *           - валюты с заданным id нет в базе данных
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void updateTest_whenCurrencyIsNonexistent_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("PLZ", "Polski zloty");
        currency.setId(99);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_UPDATE_CURRENCY_NONEXISTENT"));
        //WHEN-THEN
        currencyDao.update(currency);
    }

    /*Сценарий: - изменение в базе данных информации о валюте;
    *           - валюта равна null
    * Результат: исключение NullPointerException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void updateTest_whenCurrencyEqualsNull_thenException() throws Exception {
        //GIVEN
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("currency is marked @NonNull but is null");
        //WHEN-THEN
        currencyDao.update(null);
    }

    /*Сценарий: - удаление из базы данных информации о валюте;
    *           - валюта с заданным id существует и не имеет ссылок на себя в других таблицаях
    * Результат: удаление выполнено успешно.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void deleteTest_whenCurrencyExists_thenOk() throws Exception {
        //GIVEN
        final int ID = 1;
        Currency beforeDelete = currencyDao.getById(ID);
        //WHEN
        currencyDao.delete(ID);
        //THEN
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("EMPTY_RESULTSET") + Currency.class);
        currencyDao.getById(ID);
    }

    /*Сценарий: - удаление из базы данных информации о валюте;
    *           - валюта с заданным id не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void deleteTest_whenCurrencyNonexistent_thenException() throws Exception {
        //GIVEN
        final int ID = 99;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_UPDATE_CURRENCY_NONEXISTENT"));
        //WHEN-THEN
        currencyDao.delete(ID);
    }

    /*Сценарий: - удаление из базы данных информации о валюте;
    *           - на валюту с заданным id есть ссылки в других таблицах.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void deleteTest_whenCurrencyIsReferenced_thenException() throws Exception {
        //GIVEN
        final int ID = 1;
        NbuRate nbuRate = new NbuRate(currencyDao.getById(ID), 25.0f,new Timestamp(1));
        nbuRateDao.create(nbuRate);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("INTEGRITY_VIOLATION_WHILE_DELETE_CURRENCY"));
        //WHEN-THEN
        currencyDao.delete(ID);
    }

    /*Сценарий: - получение из базы данных информации о валюте;
    *           - валюта с заданным id существует в БД.
    * Результат: валюта успешно извлечена из БД.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void getByIdTest_whenCurrencyExists_thenOk() throws Exception {
        //GIVEN
        final int ID = 1;
        Currency expected = new Currency("USD","American dollar");
        expected.setId(ID);
        //WHEN
        Currency currency = currencyDao.getById(ID);
        //THEN
        Assert.assertNotNull(currency);
        Assert.assertEquals(expected, currency);
    }

    /*Сценарий: - получение из базы данных информации о валюте;
    *           - валюта с заданным id не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void getByIdTest_whenCurrencyNonexistent_thenException() throws Exception {
        //GIVEN
        final int ID = 99;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("EMPTY_RESULTSET") + Currency.class);
        //WHEN-THEN
        currencyDao.getById(ID);
    }

    /*Сценарий: - получение из базы данных информации о всех валютах;
    *           - в БД есть хотя бы одна запись.
    * Результат: список валют успешно извлечен из БД.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void getAllTest_whenCurrenciesExist_thenOk() throws Exception {
        //GIVEN
        List<Currency> expected = new ArrayList<>();
        expected.add(currencyDao.getById(1));
        expected.add(currencyDao.getById(2));
        //WHEN
        List<Currency> currencyList = currencyDao.getAll();
        //THEN
        Assert.assertNotNull(currencyList);
        Assert.assertEquals(expected, currencyList);
    }

    /*Сценарий: - получение из базы данных информации о всех валютах;
    *           - в БД нет данных о валюте.
    * Результат: Получаем пустой список.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql"})
    public void getAllTest_whenDBIsEmpty_thenOk() throws Exception {
        //WHEN
        List<Currency> list = currencyDao.getAll();
        //THEN
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
    }
}