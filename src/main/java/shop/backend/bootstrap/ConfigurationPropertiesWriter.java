package shop.backend.bootstrap;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;

@Service
public class ConfigurationPropertiesWriter {

    private final BootstrapConfigurationService configurationService;

    public ConfigurationPropertiesWriter(
            BootstrapConfigurationService configurationService) {

        this.configurationService = configurationService;
    }

    public void save(
            String host,
            int port,
            String database,
            String username,
            String password
    ) throws IOException {

        Path configFile = configurationService.getConfigFile();

        // Create ~/.restaurant-pos if it doesn't exist
        Files.createDirectories(configFile.getParent());

        Properties properties = new Properties();

        properties.setProperty("db.host", host);
        properties.setProperty("db.port", String.valueOf(port));
        properties.setProperty("db.name", database);
        properties.setProperty("db.username", username);
        properties.setProperty("db.password", password);
        properties.setProperty("jwt.secret", generateJwtSecret());
        properties.setProperty("jwt.expiration-ms", "28800000");

        try (OutputStream outputStream =
                     Files.newOutputStream(configFile)) {

            properties.store(
                    outputStream,
                    "Restaurant POS configuration"
            );
        }
    }

    private String generateJwtSecret() {
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}