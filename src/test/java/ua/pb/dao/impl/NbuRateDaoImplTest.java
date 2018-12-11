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
import ua.pb.dao.NbuRateDao;
import ua.pb.exception.ApplicationException;
import ua.pb.model.Currency;
import ua.pb.model.NbuRate;
import ua.pb.testconfig.TestContextConfig;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/*Класс CurrencyDaoImplTest содержит интеграционные тесты для проверки
* корректности работы методов доступа к данным, относящимся к сущности
* "Курс НБУ"
*
* Автор: Бардась А.А.
* */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContextConfig.class})
@ActiveProfiles("test")
public class NbuRateDaoImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private NbuRateDao nbuRateDao;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    /*Сценарий: - добавление в базу данных информации о курсе НБУ;
    *           - все поля корректны
    * Результат: курс НБУ успешно добавлен в базу данных.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenNbuRateIsCorrect_thenOk() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(1);
        NbuRate nbuRate = new NbuRate(currency, 10.0f, new Timestamp(1));
        //WHEN
        nbuRateDao.create(nbuRate);
        //THEN
        NbuRate extracted = nbuRateDao.getById(nbuRate.getId());
        Assert.assertNotNull(extracted);
        Assert.assertEquals(nbuRate, extracted);
    }

    /*Сценарий: - добавление в базу данных информации о курсе НБУ;
    *           - валюта, на которую ссылается курс НБУ, не существует в базе данных
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenCurrencyNonexistent_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(99);
        NbuRate nbuRate = new NbuRate(currency, 10.0f, new Timestamp(1));
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_NBU_RATE"));
        //WHEN-THEN
        nbuRateDao.create(nbuRate);
    }

    /*Сценарий: - добавление в базу данных информации о курсе НБУ;
    *           - курс валюты отрицателен.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenNegativeRate_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(1);
        NbuRate nbuRate = new NbuRate(currency, -10.0f, new Timestamp(1));
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_NBU_RATE"));
        //WHEN-THEN
        nbuRateDao.create(nbuRate);
    }

    /*Сценарий: - добавление в базу данных информации о курсе НБУ;
    *           - дата равна null.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenDateEqualsNull_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(1);
        NbuRate nbuRate = new NbuRate(currency, 10.0f, null);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_NBU_RATE"));
        //WHEN-THEN
        nbuRateDao.create(nbuRate);
    }

    /*Сценарий: - добавление в базу данных информации о курсе НБУ;
    *           - объект NbuRate равен null.
    * Результат: исключение NullPointerException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenNbuRateEqulasNull_thenException() throws Exception {
        //GIVEN
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("nbuRate is marked @NonNull but is null");
        //WHEN-THEN
        nbuRateDao.create(null);
    }

    /*Сценарий: - изменение в базе данных информации о курсе НБУ;
    *           - все поля корректны
    * Результат: курс НБУ успешно изменен в базе данных.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenNbuRateIsCorrect_thenOk() throws Exception {
        //GIVEN
        Currency currency = new Currency("EUR", "Euro");
        currency.setId(2);
        NbuRate nbuRate = new NbuRate(currency, 10.0f, new Timestamp(1));
        nbuRate.setId(1);
        //WHEN
        nbuRateDao.update(nbuRate);
        //THEN
        NbuRate extracted = nbuRateDao.getById(nbuRate.getId());
        Assert.assertNotNull(extracted);
        Assert.assertEquals(nbuRate, extracted);
    }

    /*Сценарий: - изменение в базе данных информации о курсе НБУ;
    *           - валюта, на которую ссылается курс НБУ, не существует в базе данных
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenCurrencyNonexistent_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(99);
        NbuRate nbuRate = new NbuRate(currency, 10.0f, new Timestamp(1));
        nbuRate.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_NBU_RATE"));
        //WHEN-THEN
        nbuRateDao.update(nbuRate);
    }

    /*Сценарий: - изменение в базе данных информации о курсе НБУ;
    *           - курс валюты отрицателен.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenNegativeRate_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("EUR", "Euro");
        currency.setId(2);
        NbuRate nbuRate = new NbuRate(currency, -10.0f, new Timestamp(1));
        nbuRate.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_NBU_RATE"));
        //WHEN-THEN
        nbuRateDao.update(nbuRate);
    }

    /*Сценарий: - изменение в базе данных информации о курсе НБУ;
    *           - дата равна null.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenDateEqualsNull_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(1);
        NbuRate nbuRate = new NbuRate(currency, 10.0f, null);
        nbuRate.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_NBU_RATE"));
        //WHEN-THEN
        nbuRateDao.update(nbuRate);
    }

    /*Сценарий: - изменение в базе данных информации о курсе НБУ;
    *           - объект NbuRate равен null.
    * Результат: исключение NullPointerException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenNbuRateEqulasNull_thenException() throws Exception {
        //GIVEN
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("nbuRate is marked @NonNull but is null");
        //WHEN-THEN
        nbuRateDao.update(null);
    }

    /*Сценарий: - изменение в базе данных информации о курсе НБУ;
    *           - в базе данных нет записи о курсе НБУ с заданным id.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenNbuRateNonexistent_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(1);
        NbuRate nbuRate = new NbuRate(currency, 10.0f, new Timestamp(1));
        nbuRate.setId(99);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_UPDATE_NBU_RATE_NONEXISTENT"));
        //WHEN-THEN
        nbuRateDao.update(nbuRate);
    }

    /*Сценарий: - удаление из базы данных информации о курсе НБУ;
    *           - запись для курса валют с заданным id есть в БД,
    *           - на эту запись нет ссылок в других таблицах.
    * Результат: удаление произошло успешно.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void deleteTest_whenNbuRateExist_thenOk() throws Exception {
        //GIVEN
        final int ID = 3;
        NbuRate beforeDelete = nbuRateDao.getById(ID);
        //WHEN
        nbuRateDao.delete(ID);
        //THEN
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("EMPTY_RESULTSET") + NbuRate.class);
        nbuRateDao.getById(ID);
    }

    /*Сценарий: - удаление из базы данных информации о курсе НБУ;
    *           - записи для курса валют с заданным id нет в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void deleteTest_whenNbuRateNonexistent_thenException() throws Exception {
        //GIVEN
        final int ID = 99;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_UPDATE_NBU_RATE_NONEXISTENT"));
        //WHEN-THEN
        nbuRateDao.delete(ID);
    }

    /*Сценарий: - удаление из базы данных информации о курсе НБУ;
    *           - записи для курса валют с заданным id нет в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void deleteTest_whenNbuRateIsReferenced_thenException() throws Exception {
        //GIVEN
        final int ID = 1;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("INTEGRITY_VIOLATION_WHILE_DELETE_NBU_RATE"));
        //WHEN-THEN
        nbuRateDao.delete(ID);
    }

    /*Сценарий: - получение из базы данных информации о курсе НБУ;
    *           - курс НБУ с заданным id существует в БД.
    * Результат: курс НБУ успешно извлечен из БД.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getByIdTest_whenNbuRateExists_thenOk() throws Exception {
        //GIVEN
        final int NBU_RATE_ID = 1;
        final int CURRENCY_ID = 1;
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(CURRENCY_ID);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
        Date parsedTimeStamp = dateFormat.parse("2018-09-02 00:00:00:000");
        Timestamp timestamp = new Timestamp(parsedTimeStamp.getTime());
        NbuRate expected = new NbuRate(currency, 28.00f, timestamp);
        expected.setId(NBU_RATE_ID);
        //WHEN
        NbuRate nbuRate = nbuRateDao.getById(NBU_RATE_ID);
        //THEN
        Assert.assertNotNull(nbuRate);
        Assert.assertEquals(expected, nbuRate);
    }

    /*Сценарий: - получение из базы данных информации о курсе НБУ;
    *           - курса НБУ с заданным id не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getByIdTest_whenNbuRateNonexistent_thenException() throws Exception {
        //GIVEN
        final int ID = 99;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("EMPTY_RESULTSET") + NbuRate.class);
        //WHEN-THEN
        nbuRateDao.getById(ID);
    }

    /*Сценарий: - получение из базы данных всех записей о курсе НБУ;
    *           - база данных не пуста.
    * Результат: получен список объектов NbuRate
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getAllTest_whenNbuRatesExist_thenOk() throws Exception {
        //GIVEN
        List<NbuRate> expected = new ArrayList<>();
        expected.add(nbuRateDao.getById(1));
        expected.add(nbuRateDao.getById(2));
        expected.add(nbuRateDao.getById(3));
        //WHEN-THEN
        List<NbuRate> list = nbuRateDao.getAll();
        //THEN
        Assert.assertNotNull(list);
        Assert.assertEquals(expected, list);
    }

    /*Сценарий: - получение из базы данных информации о всех курсах НБУ;
    *           - база данных пуста.
    * Результат: получен пустой список.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql"})
    public void getAllTest_whenNbuRateNonexistent_thenOk() throws Exception {
        //WHEN
        List<NbuRate> nbuRateList = nbuRateDao.getAll();
        //THEN
        Assert.assertNotNull(nbuRateList);
        Assert.assertEquals(0, nbuRateList.size());
    }

    /*Сценарий: - получение из базы данных информации об актуальном
    *             курсе НБУ (по id валюты);
    *           - база данных не пуста.
    * Результат: объект NbuRate успешно извлечен из БД.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getActualByCurrencyIdTest_whenNbuRateExists_thenOk() throws Exception {
        //GIVEN
        final int CURRENCY_ID = 2;
        NbuRate expected = nbuRateDao.getById(3);
        //WHEN
        NbuRate nbuRate = nbuRateDao.getActualByCurrencyId(CURRENCY_ID);
        //THEN
        Assert.assertNotNull(nbuRate);
        Assert.assertEquals(expected, nbuRate);
    }

    /*Сценарий: - получение из базы данных информации об актуальном
    *             курсе НБУ (по id валюты);
    *           - база данных не содержит курса для заданной валюты.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getActualByCurrencyIdTest_whenNbuRateNonexistent_thenOk() throws Exception {
        //GIVEN
        final int CURRENCY_ID = 3;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("EMPTY_RESULTSET") + NbuRate.class);
        //WHEN-THEN
        nbuRateDao.getActualByCurrencyId(CURRENCY_ID);
    }

    /*Сценарий: - получение из базы данных информации об актуальных
    *             курсах НБУ для всех валют
    *           - база данных не пуста.
    * Результат: список объектов NbuRate успешно извлечен из БД.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getActualRatesTest_whenNbuRatesExist_thenOk() throws Exception {
        //GIVEN
        List<NbuRate> expected = new ArrayList<>();
        expected.add(nbuRateDao.getById(1));
        expected.add(nbuRateDao.getById(3));
        //WHEN
        List<NbuRate> nbuRateList = nbuRateDao.getActualRates();
        //THEN
        Assert.assertNotNull(nbuRateList);
        Assert.assertEquals(expected, nbuRateList);
    }

    /*Сценарий: - получение из базы данных информации об актуальных
    *             курсах НБУ для всех валют
    *           - база данных пуста.
    * Результат: получен пустой список.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql"})
    public void getActualRatesTest_whenNbuRateNonexistent_thenOk() throws Exception {
        //WHEN
        List<NbuRate> nbuRateList = nbuRateDao.getActualRates();
        //THEN
        Assert.assertNotNull(nbuRateList);
        Assert.assertEquals(0, nbuRateList.size());
    }
}