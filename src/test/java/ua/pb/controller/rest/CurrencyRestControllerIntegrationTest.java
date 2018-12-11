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
import ua.pb.dto.create.CurrencyCreateDto;
import ua.pb.model.Currency;
import ua.pb.model.Rate;
import ua.pb.model.User;
import ua.pb.testconfig.TestContextConfig;

import javax.servlet.Filter;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContextConfig.class, WebMvcConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
@TestExecutionListeners(listeners = {ServletTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class})
public class CurrencyRestControllerIntegrationTest {

    private static final String PATH = "/rest/currency";
    private static final String ID = "/{id}";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private RateDao rateDao;

    @Autowired
    private Gson gson;

    private MockMvc mvc;
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
    public void getTest_whenAuthorizedAndCurrencyExists_thenOk() throws Exception {
        mvc.perform(get(PATH + ID, currency.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CURRENCY_CODE));
    }

    @Test
    public void getTest_whenNotAuthorized_thenFound() throws Exception {
        mvc.perform(get(PATH + ID, currency.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isFound());
    }

    @Test
    public void getTest_whenAuthorizedAndCurrencyNonexistent_thenNotFound() throws Exception {
        mvc.perform(get(PATH + ID, 999)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        mvc.perform(get(PATH + ID, currency.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void postTest_whenAuthorizedAndCurrencyIsUnique_thenCreated() throws Exception {
        final String NEW_CODE = "GBP";
        CurrencyCreateDto currencyCreateDto = new CurrencyCreateDto(NEW_CODE, "Pound"
                , 9.f, SALE);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(currencyCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(NEW_CODE));
    }

    @Test
    public void postTest_whenAuthorizedAndCurrencyNotUnique_thenConflict() throws Exception {
        CurrencyCreateDto currencyCreateDto = new CurrencyCreateDto(CURRENCY_CODE, "Pound"
                , 9.f, SALE);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(currencyCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void postTest_whenNotAuthorized_thenFound() throws Exception {
        final String NEW_CODE = "GBP";
        CurrencyCreateDto currencyCreateDto = new CurrencyCreateDto(NEW_CODE, "Pound"
                , 9.f, SALE);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(currencyCreateDto)))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void postTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        final String NEW_CODE = "GBP";
        CurrencyCreateDto currencyCreateDto = new CurrencyCreateDto(NEW_CODE, "Pound"
                , 9.f, SALE);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN"))
                .content(gson.toJson(currencyCreateDto)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void putTest_whenAuthorizedAndRateIsValid_thenOk() throws Exception {
        final float NEW_PRICE = 14.f;
        CurrencyCreateDto currencyCreateDto = new CurrencyCreateDto(CURRENCY_CODE, "Pound"
                , 9.f, NEW_PRICE);

        mvc.perform(put(PATH + ID, currency.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(currencyCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CURRENCY_CODE));
    }

    @Test
    public void putTest_whenNotAuthorized_thenFound() throws Exception {
        final String NEW_CODE = "GBP";
        CurrencyCreateDto currencyCreateDto = new CurrencyCreateDto(NEW_CODE, "Pound"
                , 9.f, SALE);

        mvc.perform(put(PATH + ID, currency.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(currencyCreateDto)))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void putTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        final String NEW_CODE = "GBP";
        CurrencyCreateDto currencyCreateDto = new CurrencyCreateDto(NEW_CODE, "Pound"
                , 9.f, SALE);

        mvc.perform(put(PATH + ID, currency.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN"))
                .content(gson.toJson(currencyCreateDto)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void deleteTest_whenAuthorized_thenNoContent() throws Exception {
        mvc.perform(delete(PATH + ID, currency.getId())
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
        mvc.perform(delete(PATH + ID, currency.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void deleteTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        mvc.perform(delete(PATH + ID, currency.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }
}