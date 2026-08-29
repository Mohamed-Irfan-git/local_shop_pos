package shop.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemController {

    private final DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {

        try (Connection connection = dataSource.getConnection()) {

            if (connection.isValid(2)) {
                return ResponseEntity.ok(
                        Map.of(
                                "server", "UP",
                                "database", "UP"
                        )
                );
            }

        } catch (Exception e) {
            return ResponseEntity
                    .status(503)
                    .body(Map.of(
                            "server", "UP",
                            "database", "DOWN"
                    ));
        }

        return ResponseEntity
                .status(503)
                .body(Map.of(
                        "server", "UP",
                        "database", "DOWN"
                ));
    }
}