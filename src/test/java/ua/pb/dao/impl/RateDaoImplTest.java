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
import ua.pb.dao.RateDao;
import ua.pb.exception.ApplicationException;
import ua.pb.model.Currency;
import ua.pb.model.Rate;
import ua.pb.testconfig.TestContextConfig;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/*Класс CurrencyDaoImplTest содержит интеграционные тесты для проверки
* корректности работы методов доступа к данным, относящимся к сущности
* "Курс Приватбанка"
*
* Автор: Бардась А.А.
* */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContextConfig.class})
@ActiveProfiles("test")
public class RateDaoImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private RateDao rateDao;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    /*Сценарий: - добавление в базу данных информации о курсе покупки-продажи в Приватбанке;
    *           - все поля корректны
    * Результат: курс покупки-продажи Приватбанка успешно добавлен в базу данных.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenRateIsCorrect_thenOk() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(1);
        Rate rate = new Rate(currency, new Timestamp(1),10.0f,9.0f);
        //WHEN
        rateDao.create(rate);
        //THEN
        Rate extracted = rateDao.getById(rate.getId());
        Assert.assertNotNull(extracted);
        Assert.assertEquals(rate, extracted);
    }

    /*Сценарий: - добавление в базу данных информации о курсе покупки-продажи в Приватбанке;
    *           - валюта, на которую ссылается курс Приватбанка, не существует в базе данных
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenCurrencyNonexistent_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(99);
        Rate rate = new Rate(currency, new Timestamp(1), 10.0f,9.0f);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"));
        //WHEN-THEN
        rateDao.create(rate);
    }

    /*Сценарий: - добавление в базу данных информации о курсе покупки-продажи в Приватбанке;
    *           - курс продажи валюты отрицателен.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenNegativeSaleRate_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(1);
        Rate rate = new Rate(currency, new Timestamp(1), -10.f,9.f);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"));
        //WHEN-THEN
        rateDao.create(rate);
    }

    /*Сценарий: - добавление в базу данных информации о курсе покупки-продажи в Приватбанке;
    *           - курс покупки валюты отрицателен.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenNegativeBuyRate_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(1);
        Rate rate = new Rate(currency, new Timestamp(1), 10.f,-9.f);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"));
        //WHEN-THEN
        rateDao.create(rate);
    }

    /*Сценарий: - добавление в базу данных информации о курсе покупки-продажи в Приватбанке;
    *           - дата равна null.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenDateEqualsNull_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(1);
        Rate rate = new Rate(currency, null, 10.f, 9.f);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"));
        //WHEN-THEN
        rateDao.create(rate);
    }

    /*Сценарий: - добавление в базу данных информации о курсе покупки-продажи в Приватбанке;
    *           - объект Rate равен null.
    * Результат: исключение NullPointerException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenRateEqulasNull_thenException() throws Exception {
        //GIVEN
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("rate is marked @NonNull but is null");
        //WHEN-THEN
        rateDao.create(null);
    }

    /*Сценарий: - изменение в базе данных информации о курсе покупки-продажи в Приватбанке;
    *           - все поля корректны
    * Результат: курс покупки-продажи Приватбанка успешно изменен в базе данных.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenRateIsCorrect_thenOk() throws Exception {
        //GIVEN
        Currency currency = new Currency("EUR", "Euro");
        currency.setId(2);
        Rate rate = new Rate(currency, new Timestamp(1), 10.f,9.f);
        rate.setId(1);
        //WHEN
        rateDao.update(rate);
        //THEN
        Rate extracted = rateDao.getById(rate.getId());
        Assert.assertNotNull(extracted);
        Assert.assertEquals(rate, extracted);
    }

    /*Сценарий: - изменение в базе данных информации о курсе покупки-продажи в Приватбанке;
    *           - валюта, на которую ссылается курс Приватбанка, не существует в базе данных
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenCurrencyNonexistent_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(99);
        Rate rate = new Rate(currency, new Timestamp(1), 10.f, 9.f);
        rate.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"));
        //WHEN-THEN
        rateDao.update(rate);
    }

    /*Сценарий: - изменение в базе данных информации о курсе покупки-продажи в Приватбанке;
    *           - курс продажи валюты отрицателен.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenNegativeSaleRate_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("EUR", "Euro");
        currency.setId(2);
        Rate rate = new Rate(currency, new Timestamp(1), -10.f, 9.f);
        rate.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"));
        //WHEN-THEN
        rateDao.update(rate);
    }

    /*Сценарий: - изменение в базе данных информации о курсе покупки-продажи в Приватбанке;
    *           - курс покупки валюты отрицателен.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenNegativeBuyRate_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("EUR", "Euro");
        currency.setId(2);
        Rate rate = new Rate(currency, new Timestamp(1), 10.f, -9.f);
        rate.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"));
        //WHEN-THEN
        rateDao.update(rate);
    }

    /*Сценарий: - изменение в базе данных информации о курсе покупки-продажи в Приватбанке;
    *           - дата равна null.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenDateEqualsNull_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(1);
        Rate rate = new Rate(currency, null, 10.f,9.f);
        rate.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"));
        //WHEN-THEN
        rateDao.update(rate);
    }

    /*Сценарий: - изменение в базе данных информации о курсе покупки-продажи в Приватбанке;
    *           - объект Rate равен null.
    * Результат: исключение NullPointerException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenRateEqulasNull_thenException() throws Exception {
        //GIVEN
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("rate is marked @NonNull but is null");
        //WHEN-THEN
        rateDao.update(null);
    }

    /*Сценарий: - изменение в базе данных информации о курсе покупки-продажи в Приватбанке;
    *           - в базе данных нет записи о курсе Приватбанка с заданным id.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenRateNonexistent_thenException() throws Exception {
        //GIVEN
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(1);
        Rate rate = new Rate(currency, new Timestamp(1), 10.f, 9.f);
        rate.setId(99);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_UPDATE_RATE_NONEXISTENT"));
        //WHEN-THEN
        rateDao.update(rate);
    }

    /*Сценарий: - удаление из базы данных информации о курсе покупки-продажи в Приватбанке;
    *           - запись для курса с заданным id есть в БД,
    *           - на эту запись нет ссылок в других таблицах.
    * Результат: удаление произошло успешно.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void deleteTest_whenRateExist_thenOk() throws Exception {
        //GIVEN
        final int ID = 3;
        Rate beforeDelete = rateDao.getById(ID);
        //WHEN
        rateDao.delete(ID);
        //THEN
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("EMPTY_RESULTSET") + Rate.class);
        rateDao.getById(ID);
    }

    /*Сценарий: - удаление из базы данных информации о курсе покупки-продажи в Приватбанке;
    *           - записи для курса с заданным id нет в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void deleteTest_whenRateNonexistent_thenException() throws Exception {
        //GIVEN
        final int ID = 99;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_UPDATE_RATE_NONEXISTENT"));
        //WHEN-THEN
        rateDao.delete(ID);
    }

    /*Сценарий: - удаление из базы данных информации о курсе покупки-продажи в Приватбанке;
    *           - записи для курса с заданным id нет в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void deleteTest_whenRateIsReferenced_thenException() throws Exception {
        //GIVEN
        final int ID = 1;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("INTEGRITY_VIOLATION_WHILE_DELETE_RATE"));
        //WHEN-THEN
        rateDao.delete(ID);
    }

    /*Сценарий: - получение из базы данных информации о курсе покупки-продажи в Приватбанке;
    *           - курс Приватбанка с заданным id существует в БД.
    * Результат: курс Приватбанка успешно извлечен из БД.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getByIdTest_whenRateExists_thenOk() throws Exception {
        //GIVEN
        final int RATE_ID = 1;
        final int CURRENCY_ID = 1;
        Currency currency = new Currency("USD", "American dollar");
        currency.setId(CURRENCY_ID);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
        Date parsedTimeStamp = dateFormat.parse("2018-09-02 08:00:00:000");
        Timestamp timestamp = new Timestamp(parsedTimeStamp.getTime());
        Rate expected = new Rate(currency, timestamp, 28.15f,27.85f);
        expected.setId(RATE_ID);
        //WHEN
        Rate rate = rateDao.getById(RATE_ID);
        //THEN
        Assert.assertNotNull(rate);
        Assert.assertEquals(expected, rate);
    }

    /*Сценарий: - получение из базы данных информации о курсе покупки-продажи в Приватбанке;
    *           - курса Приватбанка с заданным id не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getByIdTest_whenRateNonexistent_thenException() throws Exception {
        //GIVEN
        final int ID = 99;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("EMPTY_RESULTSET") + Rate.class);
        //WHEN-THEN
        rateDao.getById(ID);
    }

    /*Сценарий: - получение из базы данных информации о всех курсах покупки-продажи в Приватбанке;
    *           - база данных не пуста.
    * Результат: список объектов Rate успешно извлечен из БД.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getAllTest_whenRatesExist_thenOk() throws Exception {
        //GIVEN
        List<Rate> expected = new ArrayList<>();
        expected.add(rateDao.getById(1));
        expected.add(rateDao.getById(2));
        expected.add(rateDao.getById(3));
        //WHEN
        List<Rate> rateList = rateDao.getAll();
        //THEN
        Assert.assertNotNull(rateList);
        Assert.assertEquals(expected, rateList);
    }

    /*Сценарий: - получение из базы данных информации о всех курсах покупки-продажи в Приватбанке;
    *           - база данных пуста.
    * Результат: получен пустой список.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql"})
    public void getAllTest_whenRateNonexistent_thenOk() throws Exception {
        //WHEN
        List<Rate> rateList = rateDao.getAll();
        //THEN
        Assert.assertNotNull(rateList);
        Assert.assertEquals(0, rateList.size());
    }

    /*Сценарий: - получение из базы данных информации об актуальном
    *             курсе покупки-продажи валюты в Приватбанке (по id валюты);
    *           - база данных не пуста.
    * Результат: объект Rate успешно извлечен из БД.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getActualByCurrencyIdTest_whenRateExists_thenOk() throws Exception {
        //GIVEN
        final int CURRENCY_ID = 2;
        Rate expected = rateDao.getById(3);
        //WHEN
        Rate rate = rateDao.getActualByCurrencyId(CURRENCY_ID);
        //THEN
        Assert.assertNotNull(rate);
        Assert.assertEquals(expected, rate);
    }

    /*Сценарий: - получение из базы данных информации об актуальном
    *             курсе покупки-продажи валюты в Приватбанке (по id валюты);
    *           - база данных не содержит курса для заданной валюты.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getActualByCurrencyIdTest_whenRateNonexistent_thenOk() throws Exception {
        //GIVEN
        final int CURRENCY_ID = 3;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("EMPTY_RESULTSET") + Rate.class);
        //WHEN-THEN
        rateDao.getActualByCurrencyId(CURRENCY_ID);
    }

    /*Сценарий: - получение из базы данных информации об актуальных курсах
    *             покупки-продажи в Приватбанке;
    *           - база данных не пуста.
    * Результат: список объектов Rate успешно извлечен из БД.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getActualRatesTest_whenRatesExist_thenOk() throws Exception {
        //GIVEN
        List<Rate> expected = new ArrayList<>();
        expected.add(rateDao.getById(1));
        expected.add(rateDao.getById(3));
        //WHEN
        List<Rate> rateList = rateDao.getActualRates();
        //THEN
        Assert.assertNotNull(rateList);
        Assert.assertEquals(expected, rateList);
    }

    /*Сценарий: - получение из базы данных информации о всех курсах покупки-продажи в Приватбанке;
    *           - база данных пуста.
    * Результат: получен пустой список.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql"})
    public void getActualRatesTest_whenRateNonexistent_thenOk() throws Exception {
        //WHEN
        List<Rate> rateList = rateDao.getActualRates();
        //THEN
        Assert.assertNotNull(rateList);
        Assert.assertEquals(0, rateList.size());
    }
}