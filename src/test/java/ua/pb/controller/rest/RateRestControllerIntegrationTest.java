package ua.pb.controller.rest;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.pb.config.WebMvcConfig;
import ua.pb.dao.CurrencyDao;
import ua.pb.dao.RateDao;
import ua.pb.dao.UserDao;
import ua.pb.dto.create.RateCreateDto;
import ua.pb.model.Currency;
import ua.pb.model.Rate;
import ua.pb.model.User;
import ua.pb.testconfig.TestContextConfig;

import javax.servlet.Filter;
import java.sql.Timestamp;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContextConfig.class, WebMvcConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
@TestExecutionListeners(listeners = {ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class RateRestControllerIntegrationTest {

    private static final String PATH = "/rest/rate";
    private static final String ID = "/{id}";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private RateDao rateDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private Gson gson;

    private MockMvc mvc;
    private Rate rate;
    private Currency currency;
    private final float SALE = 10.1f;
    private final String CURRENCY_CODE = "USD";

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();

        userDao.create(new User("Admin", "$2a$10$m0puVcm.XzTvSQ31uajEYumc73fIDaGJG3/RTFPpDMZ3BQYtmMnrG", User.State.ADMIN));
        currency = new Currency(CURRENCY_CODE, "American dollar");
        currencyDao.create(currency);
        rate = new Rate(currency, new Timestamp(System.currentTimeMillis()), SALE, 9.f);
        rateDao.create(rate);
    }

    @After
    public void destroy() {
        List<User> userList = userDao.getAll();
        userList.forEach((user) -> userDao.delete(user.getId()));
        List<Rate> rateList = rateDao.getAll();
        rateList.forEach((rate) -> rateDao.delete(rate.getId()));
        List<Currency> currencyList = currencyDao.getAll();
        currencyList.forEach((currency) -> currencyDao.delete(currency.getId()));
    }

    @Test
    public void getTest_whenAuthorizedAndRateExists_thenOk() throws Exception {
        mvc.perform(get(PATH + ID, rate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sale").value(SALE));
    }

    @Test
    public void getTest_whenNotAuthorized_thenFound() throws Exception {
        mvc.perform(get(PATH + ID, rate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isFound());
    }

    @Test
    public void getTest_whenAuthorizedAndRateDoesNotExist_thenNotFound() throws Exception {
        mvc.perform(get(PATH + ID, 999)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        mvc.perform(get(PATH + ID, rate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void postTest_whenAuthorizedAndCurrencyIsExisting_thenCreated() throws Exception {
        RateCreateDto rateCreateDto = new RateCreateDto(CURRENCY_CODE
                , "UAH", 9.f, SALE);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(rateCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sale").value(SALE));
    }

    @Test
    public void postTest_whenAuthorizedAndCurrencyDoesNotExists_thenConflict() throws Exception {
        RateCreateDto rateCreateDto = new RateCreateDto("GBP", "UAH", 9.f, SALE);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(rateCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void postTest_whenNotAuthorized_thenFound() throws Exception {
        RateCreateDto rateCreateDto = new RateCreateDto(CURRENCY_CODE
                , "UAH", 9.f, SALE);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(rateCreateDto)))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void postTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        RateCreateDto rateCreateDto = new RateCreateDto(CURRENCY_CODE
                , "UAH", 9.f, SALE);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN"))
                .content(gson.toJson(rateCreateDto)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void putTest_whenAuthorizedAndRateIsValid_thenOk() throws Exception {
        float newPrice = 11.f;
        RateCreateDto rateCreateDto = new RateCreateDto(CURRENCY_CODE
                , "UAH", 9.f, newPrice);

        mvc.perform(put(PATH + ID, rate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(rateCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sale").value(newPrice));
    }

    @Test
    public void putTest_whenAuthorizedAndCurrencyNonexistent_thenConflict() throws Exception {
        RateCreateDto rateCreateDto = new RateCreateDto("GBP"
                , "UAH", 9.f, SALE);

        mvc.perform(put(PATH + ID, rate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(rateCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void putTest_whenNotAuthorized_thenFound() throws Exception {
        RateCreateDto rateCreateDto = new RateCreateDto(CURRENCY_CODE
                , "UAH", 9.f, SALE);

        mvc.perform(put(PATH + ID, rate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(rateCreateDto)))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void putTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        RateCreateDto rateCreateDto = new RateCreateDto(CURRENCY_CODE
                , "UAH", 9.f, SALE);

        mvc.perform(put(PATH + ID, rate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN"))
                .content(gson.toJson(rateCreateDto)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void deleteTest_whenAuthorized_thenNoContent() throws Exception {
        mvc.perform(delete(PATH + ID, rate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteTest_whenAuthorizedAndRateNonexistent_thenNotFound() throws Exception {
        mvc.perform(delete(PATH + ID, 999)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteTest_whenNotAuthorized_thenFound() throws Exception {
        mvc.perform(delete(PATH + ID, rate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void deleteTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        mvc.perform(delete(PATH + ID, rate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }
}