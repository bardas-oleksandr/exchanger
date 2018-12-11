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
import org.springframework.test.context.web.WebAppConfiguration;
import ua.pb.dao.UserDao;
import ua.pb.exception.ApplicationException;
import ua.pb.model.User;
import ua.pb.testconfig.TestContextConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/*Класс UserDaoImplTest содержит интеграционные тесты для проверки
* корректности работы методов доступа к данным, относящимся к сущности
* "Пользователь"
*
* Автор: Бардась А.А.
* */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContextConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class UserDaoImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private UserDao userDao;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    /*Сценарий: - добавление в базу данных информации о пользователе;
    *           - все поля корректны, пользователь уникален
    * Результат: пользователь успешно добавлен в базу данных.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void createTest_whenUserIsUnique_thenOk() throws Exception {
        //GIVEN
        User user = new User("new name", "password", User.State.ADMIN);
        //WHEN
        userDao.create(user);
        //THEN
        User extracted = userDao.getById(user.getId());
        Assert.assertNotNull(extracted);
        Assert.assertEquals(user, extracted);
    }

    /*Сценарий: - добавление в базу данных информации о пользователе;
    *           - имя пользователя не уникально
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void createTest_whenUserNameIsNotUnique_thenException() throws Exception {
        //GIVEN
        User user = new User("first user", "password", User.State.ADMIN);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("NOT_UNIQUE_USER"));
        //WHEN-THEN
        userDao.create(user);
    }

    /*Сценарий: - добавление в базу данных информации о пользователе;
    *           - имя пользователя == null
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void createTest_whenNameEqualsNull_thenException() throws Exception {
        //GIVEN
        User user = new User(null, "password", User.State.ADMIN);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_SAVE_USER"));
        //WHEN-THEN
        userDao.create(user);
    }

    /*Сценарий: - добавление в базу данных информации о пользователе;
    *           - пароль == null
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void createTest_whenPasswordEqualsNull_thenException() throws Exception {
        //GIVEN
        User user = new User("new user", null, User.State.ADMIN);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_SAVE_USER"));
        //WHEN-THEN
        userDao.create(user);
    }

    /*Сценарий: - добавление в базу данных информации о пользователе;
    *           - объект вставляемого пользователя равен null.
    * Результат: исключение NullPointerException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql"})
    public void createTest_whenUserEqualsNull_thenException() throws Exception {
        //GIVEN
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("user is marked @NonNull but is null");
        //THEN
        userDao.create(null);
    }

    /*Сценарий: - модификация в базе данных информации о пользователе;
    *           - пароль пользователя не изменяется;
    *           - все поля корректны, пользователь уникален
    * Результат: информация о пользователе успешно изменена в базе данных.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void updateTest_whenUserIsUnique_thenOk() throws Exception {
        //GIVEN
        User user = userDao.getById(1);
        user.setUsername("new name");
        user.setState(User.State.OPERATOR);
        //WHEN
        userDao.update(user);
        //THEN
        User extracted = userDao.getById(user.getId());
        Assert.assertNotNull(extracted);
        Assert.assertEquals(user, extracted);
    }

    /*Сценарий: - модификация в базе данных информации о пользователе;
    *           - пароль пользователя не изменяется;
    *           - пользователь с заданным ID не существует.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql"})
    public void updateTest_whenUserNonexistent_thenException() throws Exception {
        //GIVEN
        User user = new User("userName", "password", User.State.ADMIN);
        user.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_UPDATE_USER_NONEXISTENT"));
        //WHEN
        userDao.update(user);
    }

    /*Сценарий: - модификация в базе данных информации о пользователе;
    *           - пароль пользователя не изменяется;
    *           - имя пользователя не уникально
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void updateTest_whenUserNameIsNotUnique_thenException() throws Exception {
        //GIVEN
        User user = new User("second user", "new password", User.State.ADMIN);
        user.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("NOT_UNIQUE_USER"));
        //WHEN-THEN
        userDao.update(user);
    }

    /*Сценарий: - модификация в базе данных информации о пользователе;
    *           - пароль пользователя не изменяется;
    *           - имя пользователя == null
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void updateTest_whenNameEqualsNull_thenException() throws Exception {
        //GIVEN
        User user = new User(null, "password", User.State.ADMIN);
        user.setId(1);
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_SAVE_USER"));
        //WHEN-THEN
        userDao.update(user);
    }

    /*Сценарий: - модификация в базе данных информации о пользователе;
    *           - пароль пользователя не изменяется;
    *           - модифицируемый объект равен null.
    * Результат: исключение NullPointerException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql"})
    public void updateTest_whenUserEqualsNull_thenException() throws Exception {
        //GIVEN
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("user is marked @NonNull but is null");
        //THEN
        userDao.update(null);
    }

    /*Сценарий: - удаление из базы данных информации о пользователе;
    *             пользователь существует.
    * Результат: операция выполнена успешно.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void deleteTest_whenUserExists_thenOk() throws Exception {
        //GIVEN
        final int USER_ID = 1;
        User user = userDao.getById(USER_ID);
        //WHEN
        userDao.delete(USER_ID);
        //THEN
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("EMPTY_RESULTSET") + User.class);
        userDao.getById(USER_ID);
    }

    /*Сценарий: - удаление из базы данных информации о пользователе;
    *             пользователь не существует.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void deleteTest_whenUserNonexistent_thenException() throws Exception {
        //GIVEN
        final int USER_ID = 10;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("FAILED_DELETE_USER_NONEXISTENT"));
        //WHEN-THEN
        userDao.delete(USER_ID);
    }

    /*Сценарий: - получение из базы данных информации о пользователе;
    *             пользователь существует.
    * Результат: операция выполнена успешно.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void getByIdTest_whenUserExists_thenOk() throws Exception {
        //GIVEN
        final int USER_ID = 1;
        User user = new User("first user", "password", User.State.ADMIN);
        user.setId(USER_ID);
        //WHEN
        User extracted = userDao.getById(USER_ID);
        //THEN
        Assert.assertNotNull(extracted);
        Assert.assertEquals(user, extracted);
    }

    /*Сценарий: - получение из базы данных информации о пользователе;
    *             пользователь не существует.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void getByIdTest_whenUserNonexistent_thenException() throws Exception {
        //GIVEN
        final int USER_ID = 10;
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("EMPTY_RESULTSET") + User.class);
        //WHEN-THEN
        userDao.getById(USER_ID);
    }

    /*Сценарий: - получение из базы данных информации о пользователе;
    *             пользователь существует.
    * Результат: операция выполнена успешно.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void getByUsernameTest_whenUserExists_thenOk() throws Exception {
        //GIVEN
        User userById = userDao.getById(1);
        //WHEN
        User userByUsername = userDao.getByUsername("first user");
        //THEN
        Assert.assertNotNull(userByUsername);
        Assert.assertEquals(userById, userByUsername);
    }

    /*Сценарий: - получение из базы данных информации о пользователе;
    *             пользователь не существует.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void getByUsernameTest_whenUserNonexistent_thenException() throws Exception {
        //GIVEN
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("EMPTY_RESULTSET") + User.class);
        //WHEN-THEN
        userDao.getByUsername("nonexistent");
    }

    /*Сценарий: - получение из базы данных информации о пользователе;
    *           - имя пользователя равно null.
    * Результат: исключение NullPointerException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql"})
    public void getByUsernameTest_whenUsernameEqualsNull_thenException() throws Exception {
        //GIVEN
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("username is marked @NonNull but is null");
        //WHEN-THEN
        userDao.getByUsername(null);
    }

    /*Сценарий: - получение из базы данных информации обо всех пользователях;
    *             пользователи существуют.
    * Результат: операция выполнена успешно.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql", "classpath:insert_userTest.sql"})
    public void getAllTest_whenUsersExist_thenOk() throws Exception {
        //GIVEN
        List<User> expected = new ArrayList<>();
        expected.add(userDao.getById(1));
        expected.add(userDao.getById(2));
        expected.add(userDao.getById(3));
        //WHEN
        List<User> userList = userDao.getAll();
        //THEN
        Assert.assertNotNull(userList);
        Assert.assertEquals(expected.size(), userList.size());
        Assert.assertTrue(expected.contains(userList.get(0)));
        Assert.assertTrue(expected.contains(userList.get(1)));
        Assert.assertTrue(expected.contains(userList.get(2)));
    }

    /*Сценарий: - получение из базы данных информации обо всех пользователях;
    *             пользователи не существуют.
    * Результат: исключение ApplicationException.
    * */
    @Test
    @Sql({"classpath:schema_clean.sql"})
    public void getAllUsersTest_whenUsersNonexistent_thenException() throws Exception {
        //WHEN
        expectedException.expect(ApplicationException.class);
        expectedException.expectMessage(properties.getProperty("EMPTY_RESULTSET") + User.class);
        //THEN
        List<User> userList = userDao.getAll();
    }
}