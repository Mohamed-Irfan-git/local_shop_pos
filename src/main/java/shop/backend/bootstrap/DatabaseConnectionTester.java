package shop.backend.bootstrap;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class DatabaseConnectionTester {

    public TestResult test(
            String host,
            int port,
            String database,
            String username,
            String password
    ) {

        String url = String.format(
                "jdbc:postgresql://%s:%d/%s",
                host,
                port,
                database
        );

        try (Connection connection = DriverManager.getConnection(
                url,
                username,
                password
        )) {

            if (connection.isValid(2)) {
                return new TestResult(
                        true,
                        "Database connection successful"
                );
            }

            return new TestResult(
                    false,
                    "Database connection is not valid"
            );

        } catch (SQLException e) {

            return new TestResult(
                    false,
                    getReadableError(e)
            );
        }
    }

    private String getReadableError(SQLException e) {

        String sqlState = e.getSQLState();

        if ("28P01".equals(sqlState)) {
            return "Invalid database username or password";
        }

        if ("3D000".equals(sqlState)) {
            return "Database does not exist";
        }

        if ("08001".equals(sqlState)
                || "08006".equals(sqlState)) {
            return "Could not connect to PostgreSQL server";
        }

        if (e.getMessage() != null) {
            return e.getMessage();
        }

        return "Database connection failed";
    }

    public record TestResult(
            boolean success,
            String message
    ) {
    }
}