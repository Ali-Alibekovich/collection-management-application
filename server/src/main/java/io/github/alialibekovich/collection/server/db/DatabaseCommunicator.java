package io.github.alialibekovich.collection.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens the PostgreSQL connection and wires up the repositories.
 *
 * <p>Connection parameters are taken from the environment ({@code DB_URL},
 * {@code DB_USER}, {@code DB_PASSWORD}) with defaults matching the bundled
 * {@code docker-compose.yml}, so a local run works out of the box.</p>
 */
public class DatabaseCommunicator {

    private static final Logger log = LoggerFactory.getLogger(DatabaseCommunicator.class);

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/collection";
    private static final String DEFAULT_USER = "collection";
    private static final String DEFAULT_PASSWORD = "collection";

    private static Connection connection;
    private static OrganizationsRepository organizations;
    private static UsersRepository users;

    public void start() {
        String url = envOrDefault("DB_URL", DEFAULT_URL);
        String user = envOrDefault("DB_USER", DEFAULT_USER);
        String password = envOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
        try {
            connection = DriverManager.getConnection(url, user, password);
            users = new UsersRepository(connection);
            organizations = new OrganizationsRepository(connection);
            log.info("Connected to the database at {}", url);
        } catch (SQLException e) {
            log.error("Failed to connect to the database at {}", url, e);
            System.exit(1);
        }
    }

    public static OrganizationsRepository getOrganizations() {
        return organizations;
    }

    public static UsersRepository getUsers() {
        return users;
    }

    private static String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }
}
