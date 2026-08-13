package io.github.alialibekovich.collection.server.db;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers(disabledWithoutDocker = true)
class UsersRepositoryIT {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse(System.getProperty("postgres.image", "postgres:16-alpine"))
                    .asCompatibleSubstituteFor("postgres"));

    private static Connection connection;
    private static UsersRepository users;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        users = new UsersRepository(connection);
    }

    @Test
    void registeredUserPassesCredentialCheck() throws SQLException {
        users.addUser("alice", "password-1", "0x990000ff");

        assertTrue(users.checkCredentials("alice", "password-1"));
        assertFalse(users.checkCredentials("alice", "password-2"));
    }

    @Test
    void anotherUsersPasswordIsRejected() throws SQLException {
        // regression guard: credentials must be checked as a pair, not column by column
        users.addUser("bob", "bob-password", "0x00ff00ff");
        users.addUser("carol", "carol-password", "0x0000ffff");

        assertFalse(users.checkCredentials("bob", "carol-password"));
        assertTrue(users.checkCredentials("bob", "bob-password"));
    }

    @Test
    void unknownUserFailsCredentialCheckAndGetsDefaultColor() {
        assertFalse(users.checkCredentials("nobody", "whatever"));
        assertEquals("0xFFFFFFff", users.getColor("nobody"));
    }

    @Test
    void loginAndColorUniquenessChecksSeeStoredUsers() throws SQLException {
        users.addUser("dave", "dave-password", "0x123456ff");

        assertTrue(users.loginExists("dave"));
        assertFalse(users.loginExists("someone-else"));
        assertTrue(users.colorTaken("0x123456ff"));
        assertFalse(users.colorTaken("0x654321ff"));
        assertEquals("0x123456ff", users.getColor("dave"));
    }
}
