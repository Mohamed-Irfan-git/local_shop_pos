package shop.backend;

import org.springframework.boot.SpringApplication;

import java.nio.file.Files;
import java.nio.file.Path;

public class BackendApplication {

    public static void main(String[] args) {

        Path configFile = Path.of(
                System.getProperty("user.home"),
                ".restaurant-pos",
                "config.properties"
        );

        Class<?> applicationClass;

        if (Files.isRegularFile(configFile)) {
            applicationClass = NormalApplication.class;
        } else {
            applicationClass = SetupApplication.class;
        }

        SpringApplication application =
                new SpringApplication(applicationClass);

        boolean configured =
                applicationClass == NormalApplication.class;

        java.util.Map<String, Object> defaultProperties =
                new java.util.HashMap<>();

        defaultProperties.put(
                "spring.profiles.active",
                configured ? "normal" : "setup"
        );

        if (configured) {
            defaultProperties.put(
                    "spring.config.additional-location",
                    "file:" + configFile
            );
        }

        application.setDefaultProperties(defaultProperties);

        application.run(args);
    }
}