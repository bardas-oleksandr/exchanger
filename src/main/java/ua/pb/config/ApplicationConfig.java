package ua.pb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ua.pb.exception.ApplicationException;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import java.util.Set;

@Configuration
@ComponentScan({"ua.pb.converter", "ua.pb.dao", "ua.pb.service", "ua.pb.dto.create"})
@Import(SecurityConfig.class)
public class ApplicationConfig {

    @Autowired
    private BasePooledObjectFactory<Connection> connectionFactory;

    @Autowired
    private Set<Converter<?,?>> converterSet;

    @Bean
    public Properties applicationProperties() {
        return loadProperties("application.properties");
    }

    @Bean
    public Properties messageProperties() {
        return loadProperties(applicationProperties()
                .getProperty("exception.message.properties"));
    }

    @Bean
    public GenericObjectPool<Connection> connectionPool() {
        return new GenericObjectPool<Connection>(connectionFactory);
    }

    @Bean
    @Profile("!test")
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScripts(applicationProperties().getProperty("create.script")
                        , applicationProperties().getProperty("insert.script"))
                .build();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Connection connection() throws Exception {
        return connectionPool().borrowObject();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource());
    }

    @Bean("conversionService")
    public ConversionServiceFactoryBean conversionServiceFactoryBean(){
        ConversionServiceFactoryBean factoryBean = new ConversionServiceFactoryBean();
        factoryBean.setConverters(converterSet);
        return factoryBean;
    }

    @Bean("validator")
    public LocalValidatorFactoryBean localValidatorFactoryBean(){
        return new LocalValidatorFactoryBean();
    }

    private Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        try (InputStream stream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileName)) {
            properties.load(stream);
        } catch (IOException e) {
            throw new ApplicationException("Failed to read file: " + fileName, e);
        }
        return properties;
    }

    @Bean
    public Gson gson(){
        return new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }
}
