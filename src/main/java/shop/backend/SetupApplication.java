package shop.backend;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import shop.backend.config.SetupSecurityConfig;
import shop.backend.controller.SetupController;

@Profile("setup")
@SpringBootApplication(
        scanBasePackages = "shop.backend.bootstrap",
        exclude = {
                DataSourceAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class,
                JdbcTemplateAutoConfiguration.class
        }
)
@Import({
        SetupController.class,
        SetupSecurityConfig.class
})
public class SetupApplication {
}