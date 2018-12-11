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
import ua.pb.dao.OperationDao;
import ua.pb.dao.RateDao;
import ua.pb.dao.UserDao;
import ua.pb.exception.ApplicationException;
import ua.pb.model.*;
import ua.pb.testconfig.TestContextConfig;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/*Класс CurrencyDaoImplTest содержит интеграционные тесты для проверки
* корректности работы методов доступа к данным, относящимся к сущности
* "Операция покупки-продажи"
*
* Автор: Бардась А.А.
* */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContextConfig.class})
@ActiveProfiles("test")
public class OperationDaoImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private OperationDao operationDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RateDao rateDao;

    @Autowired
    private NbuRateDao nbuRateDao;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    /*Сценарий: - добавление в базу данных информации об операции покупки-продажи;
    *           - все поля корректны
    * Результат: запись об операции успешно добавлена в базу данных.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenOperationIsCorrect_thenOk() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = nbuRateDao.getById(1);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, new Timestamp(1), false);
        //WHEN
        operationDao.create(operation);
        //THEN
        Operation extracted = operationDao.getById(operation.getId());
        Assert.assertNotNull(extracted);
        Assert.assertEquals(operation, extracted);
    }

    /*Сценарий: - добавление в базу данных информации об операции покупки-продажи;
    *           - пользователь, на которого ссылается объект Operation, не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenUserNonexistent_thenException() throws Exception {
        //GIVEN
        User user = new User();
        user.setId(99);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = nbuRateDao.getById(1);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, new Timestamp(1), false);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.create(operation);
    }

    /*Сценарий: - добавление в базу данных информации об операции покупки-продажи;
    *           - курс валют, на который ссылается объект Operation, не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenRateNonexistent_thenException() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        Rate rate = new Rate();
        rate.setId(99);
        NbuRate nbuRate = nbuRateDao.getById(1);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, new Timestamp(1), false);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.create(operation);
    }

    /*Сценарий: - добавление в базу данных информации об операции покупки-продажи;
    *           - курс НБУ, на который ссылается объект Operation, не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenNbuRateNonexistent_thenException() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = new NbuRate();
        nbuRate.setId(99);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, new Timestamp(1), false);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.create(operation);
    }

    /*Сценарий: - добавление в базу данных информации об операции покупки-продажи;
    *           - сумма в гривне, указанная в операции отрицательна.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenSumHrnIsNegative_thenException() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = nbuRateDao.getById(1);
        Operation operation = new Operation(rate, nbuRate, user, true
                , -2815.f, 100.f, new Timestamp(1), false);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.create(operation);
    }

    /*Сценарий: - добавление в базу данных информации об операции покупки-продажи;
    *           - сумма в валюте, указанная в операции отрицательна.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenSumCurrIsNegative_thenException() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = nbuRateDao.getById(1);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, -100.f, new Timestamp(1), false);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.create(operation);
    }

    /*Сценарий: - добавление в базу данных информации об операции покупки-продажи;
    *           - дата операции равна null.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenDateEqualsNull_thenException() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = nbuRateDao.getById(1);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, null, false);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.create(operation);
    }

    /*Сценарий: - добавление в базу данных информации об операции покупки-продажи;
    *           - дата операции равна null.
    * Результат: исключение NullPointerException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void createTest_whenOperationEqualsNull_thenException() throws Exception {
        //GIVEN
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("operation is marked @NonNull but is null");
        //WHEN-THEN
        operationDao.create(null);
    }


    /*Сценарий: - изменение в базе данных информации об операции покупки-продажи;
    *           - все поля корректны
    * Результат: запись об операции успешно изменена в базе данных.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenOperationIsCorrect_thenOk() throws Exception {
        //GIVEN
        User user = userDao.getById(2);
        Rate rate = rateDao.getById(2);
        NbuRate nbuRate = nbuRateDao.getById(2);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, new Timestamp(1), true);
        operation.setId(1);
        //WHEN
        operationDao.update(operation);
        //THEN
        Operation extracted = operationDao.getById(operation.getId());
        Assert.assertNotNull(extracted);
        Assert.assertEquals(operation, extracted);
    }

    /*Сценарий: - изменение в базе данных информации об операции покупки-продажи;
    *           - пользователь, на которого ссылается объект Operation, не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenUserNonexistent_thenException() throws Exception {
        //GIVEN
        User user = new User();
        user.setId(99);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = nbuRateDao.getById(1);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, new Timestamp(1), false);
        operation.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.update(operation);
    }

    /*Сценарий: - изменение в базе данных информации об операции покупки-продажи;
    *           - курс валют, на который ссылается объект Operation, не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenRateNonexistent_thenException() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        Rate rate = new Rate();
        rate.setId(99);
        NbuRate nbuRate = nbuRateDao.getById(1);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, new Timestamp(1), false);
        operation.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.update(operation);
    }

    /*Сценарий: - изменение в базе данных информации об операции покупки-продажи;
    *           - курс НБУ, на который ссылается объект Operation, не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenNbuRateNonexistent_thenException() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = new NbuRate();
        nbuRate.setId(99);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, new Timestamp(1), false);
        operation.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.update(operation);
    }

    /*Сценарий: - изменение в базе данных информации об операции покупки-продажи;
    *           - сумма в гривне, указанная в операции отрицательна.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenSumHrnIsNegative_thenException() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = nbuRateDao.getById(1);
        Operation operation = new Operation(rate, nbuRate, user, true
                , -2815.f, 100.f, new Timestamp(1), false);
        operation.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.update(operation);
    }

    /*Сценарий: - изменение в базе данных информации об операции покупки-продажи;
    *           - сумма в валюте, указанная в операции отрицательна.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenSumCurrIsNegative_thenException() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = nbuRateDao.getById(1);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, -100.f, new Timestamp(1), false);
        operation.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.update(operation);
    }

    /*Сценарий: - изменение в базе данных информации об операции покупки-продажи;
    *           - дата операции равна null.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenDateEqualsNull_thenException() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = nbuRateDao.getById(1);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, null, false);
        operation.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"));
        //WHEN-THEN
        operationDao.update(operation);
    }

    /*Сценарий: - изменение в базе данных информации об операции покупки-продажи;
    *           - дата операции равна null.
    * Результат: исключение NullPointerException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenOperationEqualsNull_thenException() throws Exception {
        //GIVEN
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("operation is marked @NonNull but is null");
        //WHEN-THEN
        operationDao.update(null);
    }

    /*Сценарий: - изменение в базе данных информации об операции покупки-продажи;
    *           - операции с заданным id не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void updateTest_whenOperationNonexistent_thenException() throws Exception {
        //GIVEN
        User user = userDao.getById(2);
        Rate rate = rateDao.getById(2);
        NbuRate nbuRate = nbuRateDao.getById(2);
        Operation operation = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, new Timestamp(1), true);
        operation.setId(99);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_UPDATE_OPERATION_NONEXISTENT"));
        //WHEN-THEN
        operationDao.update(operation);
    }

    /*Сценарий: - получение из базы данных информации об операции покупки-продажи;
    *           - операция покупки-продажи с заданным id существует в БД.
    * Результат: операция покупки-продажи успешно извлечена из БД.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_operationTest.sql"})
    public void getByIdTest_whenOperationExists_thenOk() throws Exception {
        //GIVEN
        final int OPERATION_ID = 1;
        User user = userDao.getById(2);
        Rate rate = rateDao.getById(1);
        NbuRate nbuRate = nbuRateDao.getById(1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
        Date parsedTimeStamp = dateFormat.parse("2018-09-02 13:05:00:000");
        Timestamp timestamp = new Timestamp(parsedTimeStamp.getTime());
        Operation expected = new Operation(rate, nbuRate, user, true
                , 2815.f, 100.f, timestamp, false);
        expected.setId(OPERATION_ID);
        //WHEN
        Operation operation = operationDao.getById(OPERATION_ID);
        //THEN
        Assert.assertNotNull(operation);
        Assert.assertEquals(expected, operation);
    }

    /*Сценарий: - получение из базы данных информации об операции покупки-продажи;
    *           - операции покупки-продажи с заданным id не существует в БД.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_currencyTest.sql"})
    public void getByIdTest_whenOperationNonexistent_thenException() throws Exception {
        //GIVEN
        final int ID = 99;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties
                .getProperty("EMPTY_RESULTSET") + Operation.class);
        //WHEN-THEN
        operationDao.getById(ID);
    }
}