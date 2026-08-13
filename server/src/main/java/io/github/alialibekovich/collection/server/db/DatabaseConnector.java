package io.github.alialibekovich.collection.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens the PostgreSQL connection from the environment ({@code DB_URL},
 * {@code DB_USER}, {@code DB_PASSWORD}) with defaults matching the bundled
 * {@code docker-compose.yml}.
 */
public final class DatabaseConnector {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConnector.class);

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/collection";
    private static final String DEFAULT_USER = "collection";
    private static final String DEFAULT_PASSWORD = "collection";

    private DatabaseConnector() {
    }

    public static Connection connect() throws SQLException {
        String url = envOrDefault("DB_URL", DEFAULT_URL);
        Connection connection = DriverManager.getConnection(
                url, envOrDefault("DB_USER", DEFAULT_USER), envOrDefault("DB_PASSWORD", DEFAULT_PASSWORD));
        log.info("Connected to the database at {}", url);
        return connection;
    }

    private static String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }
}
