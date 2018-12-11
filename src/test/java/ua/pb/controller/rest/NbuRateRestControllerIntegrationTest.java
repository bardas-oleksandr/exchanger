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
import ua.pb.dao.NbuRateDao;
import ua.pb.dao.UserDao;
import ua.pb.dto.create.NbuRateCreateDto;
import ua.pb.model.Currency;
import ua.pb.model.NbuRate;
import ua.pb.model.User;
import ua.pb.testconfig.TestContextConfig;

import javax.servlet.Filter;
import java.sql.Timestamp;
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
public class NbuRateRestControllerIntegrationTest {

    private static final String PATH = "/rest/nburate";
    private static final String ID = "/{id}";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private NbuRateDao nbuRateDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private Gson gson;

    private MockMvc mvc;
    private NbuRate nbuRate;
    private Currency currency;
    private final float PRICE = 10.1f;
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
        nbuRate = new NbuRate(currency, PRICE, new Timestamp(System.currentTimeMillis()));
        nbuRateDao.create(nbuRate);
    }

    @After
    public void destroy() {
        List<User> userList = userDao.getAll();
        userList.forEach((user) -> userDao.delete(user.getId()));
        List<NbuRate> nbuRateList = nbuRateDao.getAll();
        nbuRateList.forEach((nbuRate) -> nbuRateDao.delete(nbuRate.getId()));
        List<Currency> currencyList = currencyDao.getAll();
        currencyList.forEach((currency) -> currencyDao.delete(currency.getId()));
    }

    @Test
    public void getTest_whenAuthorizedAndNbuRateExists_thenOk() throws Exception {
        mvc.perform(get(PATH + ID, nbuRate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(PRICE));
    }

    @Test
    public void getTest_whenNotAuthorized_thenFound() throws Exception {
        mvc.perform(get(PATH + ID, nbuRate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isFound());
    }

    @Test
    public void getTest_whenAuthorizedAndNbuRateNonexistent_thenNotFound() throws Exception {
        mvc.perform(get(PATH + ID, 999)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        mvc.perform(get(PATH + ID, nbuRate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void postTest_whenAuthorizedAndCurrencyIsExisting_thenCreated() throws Exception {
        NbuRateCreateDto nbuRateCreateDto = new NbuRateCreateDto(1, "American dollar"
                , PRICE, CURRENCY_CODE, "11.12.2018");

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(nbuRateCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(PRICE));
    }

    @Test
    public void postTest_whenAuthorizedAndCurrencyDoesNotExists_thenConflict() throws Exception {
        NbuRateCreateDto nbuRateCreateDto = new NbuRateCreateDto(1, "American dollar"
                , PRICE, "GBP", "11.12.2018");

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(nbuRateCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void postTest_whenNotAuthorized_thenFound() throws Exception {
        NbuRateCreateDto nbuRateCreateDto = new NbuRateCreateDto(1, "American dollar"
                , PRICE, CURRENCY_CODE, "11.12.2018");

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(nbuRateCreateDto)))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void postTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        NbuRateCreateDto nbuRateCreateDto = new NbuRateCreateDto(1, "American dollar"
                , PRICE, CURRENCY_CODE, "11.12.2018");

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN"))
                .content(gson.toJson(nbuRateCreateDto)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void putTest_whenAuthorizedAndRateIsValid_thenOk() throws Exception {
        float newPrice = 11.f;
        NbuRateCreateDto nbuRateCreateDto = new NbuRateCreateDto(1, "American dollar"
                , newPrice, CURRENCY_CODE, "11.12.2018");

        mvc.perform(put(PATH + ID, nbuRate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(nbuRateCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(newPrice));
    }

    @Test
    public void putTest_whenAuthorizedAndCurrencyNonexistent_thenConflict() throws Exception {
        NbuRateCreateDto nbuRateCreateDto = new NbuRateCreateDto(1, "American dollar"
                , PRICE, "GBP", "11.12.2018");

        mvc.perform(put(PATH + ID, nbuRate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(nbuRateCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void putTest_whenNotAuthorized_thenFound() throws Exception {
        NbuRateCreateDto nbuRateCreateDto = new NbuRateCreateDto(1, "American dollar"
                , PRICE, CURRENCY_CODE, "11.12.2018");

        mvc.perform(put(PATH + ID, nbuRate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(nbuRateCreateDto)))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void putTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        NbuRateCreateDto nbuRateCreateDto = new NbuRateCreateDto(1, "American dollar"
                , PRICE, CURRENCY_CODE, "11.12.2018");

        mvc.perform(put(PATH + ID, nbuRate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN"))
                .content(gson.toJson(nbuRateCreateDto)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void deleteTest_whenAuthorized_thenNoContent() throws Exception {
        mvc.perform(delete(PATH + ID, nbuRate.getId())
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
        mvc.perform(delete(PATH + ID, nbuRate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void deleteTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        mvc.perform(delete(PATH + ID, nbuRate.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }
}