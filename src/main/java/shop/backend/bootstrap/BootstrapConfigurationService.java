package shop.backend.bootstrap;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class BootstrapConfigurationService {

    private final Path configFile;

    public BootstrapConfigurationService() {
        this.configFile = Path.of(
                System.getProperty("user.home"),
                ".restaurant-pos",
                "config.properties"
        );
    }

    public boolean isConfigured() {
        return Files.isRegularFile(configFile);
    }

    public Path getConfigFile() {
        return configFile;
    }
}