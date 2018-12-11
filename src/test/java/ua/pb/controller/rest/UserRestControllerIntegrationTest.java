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
import ua.pb.dao.UserDao;
import ua.pb.dto.create.UserCreateDto;
import ua.pb.model.User;
import ua.pb.testconfig.TestContextConfig;

import javax.servlet.Filter;

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
public class UserRestControllerIntegrationTest {

    private static final String PATH = "/rest/user";
    private static final String ID = "/{id}";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private UserDao userDao;

    @Autowired
    private
    Gson gson;

    private MockMvc mvc;

    private User admin;
    private final String SECOND_USER_NAME = "Bob";

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilters(springSecurityFilterChain)
                .build();

        admin = new User("Admin", "$2a$10$m0puVcm.XzTvSQ31uajEYumc73fIDaGJG3/RTFPpDMZ3BQYtmMnrG", User.State.ADMIN);
        userDao.create(admin);
        userDao.create(new User(SECOND_USER_NAME, "$2a$10$m0puVcm.XzTvSQ31uajEYumc73fIDaGJG3/RTFPpDMZ3BQYtmMnrG", User.State.ADMIN));
    }

    @After
    public void destroy() {
        List<User> list = userDao.getAll();
        list.forEach((user) -> userDao.delete(user.getId()));
    }

    @Test
    public void getTest_whenAuthorizedAndUserExists_thenOk() throws Exception {
        mvc.perform(get(PATH + ID, admin.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Admin"));
    }

    @Test
    public void getTest_whenNotAuthorized_thenFound() throws Exception {
        mvc.perform(get(PATH + ID, admin.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isFound());
    }

    @Test
    public void getTest_whenAuthorizedAndUserDoesNotExist_thenNotFound() throws Exception {
        mvc.perform(get(PATH + ID, 999)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        mvc.perform(get(PATH + ID, admin.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void postTest_whenAuthorizedAndUserIsUnique_thenCreated() throws Exception {
        final String NAME = "Jack";
        UserCreateDto user = new UserCreateDto(NAME, "1234", User.State.OPERATOR);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(user))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isCreated());
                //.andExpect(jsonPath("$.username"). value(NAME));
    }

    @Test
    public void postTest_whenAuthorizedAndUserIsNotUnique_thenConflict() throws Exception {
        final String NAME = "Admin";
        UserCreateDto user = new UserCreateDto(NAME, "1234", User.State.OPERATOR);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(user))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void postTest_whenNotAuthorized_thenFound() throws Exception {
        final String NAME = "Jack";
        UserCreateDto user = new UserCreateDto(NAME, "1234", User.State.OPERATOR);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(user)))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void postTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        final String NAME = "Jack";
        UserCreateDto user = new UserCreateDto(NAME, "1234", User.State.OPERATOR);

        mvc.perform(post(PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN"))
                .content(gson.toJson(user)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void putTest_whenAuthorizedAndUserIsUnique_thenOk() throws Exception {
        final String NAME = "Jack";
        UserCreateDto user = new UserCreateDto(NAME, "1234", User.State.ADMIN);

        mvc.perform(put(PATH + ID, admin.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(user))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isOk());
        //.andExpect(jsonPath("$.username"). value("Jack"));
    }

    @Test
    public void putTest_whenAuthorizedAndUserNonexistent_thenNotFound() throws Exception {
        final String NAME = "Jack";
        UserCreateDto user = new UserCreateDto(NAME, "1234", User.State.ADMIN);

        mvc.perform(put(PATH + ID, 999)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(user))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void putTest_whenAuthorizedAndUserNameIsNotUnique_thenConflict() throws Exception {
        UserCreateDto user = new UserCreateDto(SECOND_USER_NAME, "1234", User.State.ADMIN);

        mvc.perform(put(PATH + ID, admin.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(user))
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void putTest_whenNotAuthorized_thenFound() throws Exception {
        final String NAME = "Jack";
        UserCreateDto user = new UserCreateDto(NAME, "1234", User.State.ADMIN);

        mvc.perform(put(PATH + ID, admin.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(gson.toJson(user)))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void putTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        final String NAME = "Jack";
        UserCreateDto user = new UserCreateDto(NAME, "1234", User.State.ADMIN);

        mvc.perform(put(PATH + ID, admin.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN"))
                .content(gson.toJson(user)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void deleteTest_whenAuthorized_thenNoContent() throws Exception {
        mvc.perform(delete(PATH + ID, admin.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteTest_whenAuthorizedAndUserDoesNotExist_thenNotFound() throws Exception {
        mvc.perform(delete(PATH + ID, 999)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteTest_whenNotAuthorized_thenFound() throws Exception {
        mvc.perform(delete(PATH + ID, admin.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isFound());
    }

    @Test
    public void deleteTest_whenAuthorizedAndNoContentType_thenUnsupportedMediaType() throws Exception {
        mvc.perform(delete(PATH + ID, admin.getId())
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .with(user("Admin").password("admin").roles("ADMIN")))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }
}