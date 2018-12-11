package ua.pb.testconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import ua.pb.config.ApplicationConfig;

import javax.sql.DataSource;

@ContextConfiguration
@Import({ApplicationConfig.class})
@Profile("test")
public class TestContextConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScripts("classpath:schema_create.sql")
                .build();
    }
}
