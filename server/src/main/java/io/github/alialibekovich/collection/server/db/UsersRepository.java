package io.github.alialibekovich.collection.server.db;

import io.github.alialibekovich.collection.server.core.UserStore;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Repository for user accounts: registration, credential verification and
 * profile colour lookup.
 *
 * <p>Passwords are stored as bcrypt hashes (salt is embedded in the hash),
 * so the same password produces a different hash for every user.</p>
 */
public class UsersRepository implements UserStore {

    private static final Logger log = LoggerFactory.getLogger(UsersRepository.class);

    private static final String DEFAULT_COLOR = "0xFFFFFFff";

    private final Connection connection;

    public UsersRepository(Connection connection) throws SQLException {
        this.connection = connection;
        createTableIfAbsent();
    }

    private void createTableIfAbsent() throws SQLException {
        String ddl = "CREATE TABLE IF NOT EXISTS users ("
                + "login TEXT PRIMARY KEY, "
                + "password TEXT NOT NULL, "
                + "color TEXT NOT NULL)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(ddl);
        }
    }

    @Override
    public void addUser(String login, String password, String color) throws SQLException {
        String sql = "INSERT INTO users (login, password, color) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            statement.setString(2, hashPassword(password));
            statement.setString(3, color);
            statement.execute();
        }
    }

    /**
     * Verifies a login/password pair against the stored bcrypt hash.
     * The pair is checked as a whole: a password of another user never matches.
     */
    @Override
    public boolean checkCredentials(String login, String password) {
        String sql = "SELECT password FROM users WHERE login = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && BCrypt.checkpw(password, resultSet.getString(1));
            }
        } catch (SQLException e) {
            log.error("Failed to verify credentials of user '{}'", login, e);
            return false;
        }
    }

    @Override
    public boolean loginExists(String login) throws SQLException {
        return exists("SELECT 1 FROM users WHERE login = ?", login);
    }

    /** The profile colour doubles as a user marker in the visualization, hence the uniqueness check. */
    @Override
    public boolean colorTaken(String color) throws SQLException {
        return exists("SELECT 1 FROM users WHERE color = ?", color);
    }

    @Override
    public String getColor(String login) {
        String sql = "SELECT color FROM users WHERE login = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            log.error("Failed to read colour of user '{}'", login, e);
        }
        return DEFAULT_COLOR;
    }

    private boolean exists(String sql, String value) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
