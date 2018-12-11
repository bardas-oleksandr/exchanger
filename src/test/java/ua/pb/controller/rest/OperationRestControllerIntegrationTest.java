package ua.pb.controller.rest;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.annotation.DirtiesContext;
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
import ua.pb.dao.*;
import ua.pb.dto.create.OperationCreateDto;
import ua.pb.model.*;
import ua.pb.testconfig.TestContextConfig;

import javax.servlet.Filter;
import java.sql.Timestamp;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
@DirtiesContext
public class OperationRestControllerIntegrationTest {

    private static final String PATH = "/rest/operation";
    private static final String ID = "/{id}";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private OperationDao operationDao;

    @Autowired
    private NbuRateDao nbuRateDao;

    @Autowired
    private RateDao rateDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private Gson gson;

    private MockMvc mvc;
    private Operation operation;
    private User user;
    private NbuRate nbuRate;
    private Rate rate;
    private Currency currency;
    private final float SALE = 10.1f;
    private final float PRICE = 10.05f;
    private final float SUM_HRN = 101.1f;
    private final String CURRENCY_CODE = "USD";

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();

        user = new User("Admin", "$2a$10$m0puVcm.XzTvSQ31uajEYumc73fIDaGJG3/RTFPpDMZ3BQYtmMnrG"
                , User.State.ADMIN);
        userDao.create(user);
        currency = new Currency(CURRENCY_CODE, "American dollar");
        currencyDao.create(currency);
        nbuRate = new NbuRate(currency, PRICE, new Timestamp(System.currentTimeMillis()));
        nbuRateDao.create(nbuRate);
        rate = new Rate(currency, new Timestamp(System.currentTimeMillis()), SALE, 9.f);
        rateDao.create(rate);
        operation = new Operation(rate, nbuRate, user, true, SUM_HRN, 12.f
                , new Timestamp(System.currentTimeMillis()), false);
        operationDao.create(operation);
    }

    @Test
    @DirtiesContext
    public void getTest_whenAuthorizedAndOperationExists_thenOk() throws Exception {
        mvc.perform(get(PATH + ID, operation.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sumHrn").value(SUM_HRN));
    }

    @Test
    @DirtiesContext
    public void getTest_whenNotAuthorized_thenFound() throws Exception {
        mvc.perform(get(PATH + ID, operation.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isFound());
    }

    @Test
    @DirtiesContext
    public void getTest_whenAuthorizedAndOperationNonexistent_thenNotFound() throws Exception {
        mvc.perform(get(PATH + ID, 999)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    public void getTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        mvc.perform(get(PATH + ID, operation.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DirtiesContext
    public void postTest_whenAuthorizedAndRateIsExisting_thenCreated() throws Exception {
        OperationCreateDto operationCreateDto = new OperationCreateDto(rate.getId()
                ,nbuRate.getId(),true,SUM_HRN, 100.f);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(operationCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sumHrn").value(SUM_HRN));
    }

    @Test
    @DirtiesContext
    public void postTest_whenAuthorizedAndRateNonexistent_thenConflict() throws Exception {
        final int RATE_ID = 999;
        OperationCreateDto operationCreateDto = new OperationCreateDto(RATE_ID
                ,nbuRate.getId(),true,SUM_HRN, 100.f);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(operationCreateDto))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DirtiesContext
    public void postTest_whenNotAuthorized_thenFound() throws Exception {
        OperationCreateDto operationCreateDto = new OperationCreateDto(rate.getId()
                ,nbuRate.getId(),true,SUM_HRN, 100.f);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(operationCreateDto)))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    @DirtiesContext
    public void postTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        OperationCreateDto operationCreateDto = new OperationCreateDto(rate.getId()
                ,nbuRate.getId(),true,SUM_HRN, 100.f);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN"))
                .content(gson.toJson(operationCreateDto)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DirtiesContext
    public void putTest_whenAuthorizedAndIsDeleted_thenOk() throws Exception {
        OperationCreateDto operationCreateDto = new OperationCreateDto(rate.getId()
                ,nbuRate.getId(),true,SUM_HRN, 100.f);

        mvc.perform(put(PATH + ID, operation.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(operationCreateDto))
                .param("isDeleted","on")
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted").value(true));
    }

    @Test
    @DirtiesContext
    public void putTest_whenNotAuthorized_thenFound() throws Exception {
        OperationCreateDto operationCreateDto = new OperationCreateDto(rate.getId()
                ,nbuRate.getId(),true,SUM_HRN, 100.f);

        mvc.perform(put(PATH + ID, operation.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(operationCreateDto)))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    @DirtiesContext
    public void putTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        OperationCreateDto operationCreateDto = new OperationCreateDto(rate.getId()
                ,nbuRate.getId(),true,SUM_HRN, 100.f);

        mvc.perform(put(PATH + ID, operation.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN"))
                .content(gson.toJson(operationCreateDto)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }
}