package shop.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.backend.bootstrap.BootstrapConfigurationService;
import shop.backend.bootstrap.ConfigurationPropertiesWriter;
import shop.backend.bootstrap.DatabaseConnectionTester;

import java.util.Map;

@RestController
@Profile("setup")
@RequestMapping("/api/v1/setup")
@RequiredArgsConstructor
public class SetupController {

    private final DatabaseConnectionTester databaseConnectionTester;
    private final BootstrapConfigurationService configurationService;
    private final ConfigurationPropertiesWriter configurationPropertiesWriter;

    /**
     * Check whether the application has been configured.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {

        return ResponseEntity.ok(
                Map.of(
                        "configured",
                        configurationService.isConfigured()
                )
        );
    }

    /**
     * Test PostgreSQL connection using the configuration
     * provided by the frontend.
     */
    @PostMapping("/test-database")
    public ResponseEntity<Map<String, Object>> testDatabase(
            @Valid @RequestBody DatabaseRequest request
    ) {

        DatabaseConnectionTester.TestResult result =
                databaseConnectionTester.test(
                        request.host(),
                        request.port(),
                        request.database(),
                        request.username(),
                        request.password()
                );

        if (result.success()) {

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", result.message()
                    )
            );
        }

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "success", false,
                                "message", result.message()
                        )
                );
    }

    /**
     * Test the database and save the configuration.
     *
     * We test the connection again here instead of trusting
     * the previous /test-database request.
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveConfiguration(
            @Valid @RequestBody DatabaseRequest request
    ) {

        // First verify that the database is reachable.
        DatabaseConnectionTester.TestResult result =
                databaseConnectionTester.test(
                        request.host(),
                        request.port(),
                        request.database(),
                        request.username(),
                        request.password()
                );

        if (!result.success()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Database connection failed: "
                                            + result.message()
                            )
                    );
        }

        try {

            configurationPropertiesWriter.save(
                    request.host(),
                    request.port(),
                    request.database(),
                    request.username(),
                    request.password()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message",
                            "Configuration saved successfully"
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Failed to save configuration"
                            )
                    );
        }
    }

    /**
     * Database configuration sent by the frontend.
     */
    public record DatabaseRequest(

            @NotBlank(message = "Database host is required")
            String host,

            @Min(value = 1, message = "Database port must be greater than 0")
            int port,

            @NotBlank(message = "Database name is required")
            String database,

            @NotBlank(message = "Database username is required")
            String username,

            @NotBlank(message = "Database password is required")
            String password
    ) {
    }
}
