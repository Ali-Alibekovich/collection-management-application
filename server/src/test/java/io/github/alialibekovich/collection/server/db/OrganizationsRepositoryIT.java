package io.github.alialibekovich.collection.server.db;

import io.github.alialibekovich.collection.model.Address;
import io.github.alialibekovich.collection.model.Coordinates;
import io.github.alialibekovich.collection.model.Location;
import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.model.OrganizationType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers(disabledWithoutDocker = true)
class OrganizationsRepositoryIT {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse(System.getProperty("postgres.image", "postgres:16-alpine"))
                    .asCompatibleSubstituteFor("postgres"));

    private static Connection connection;
    private static OrganizationsRepository organizations;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        organizations = new OrganizationsRepository(connection);
    }

    @Test
    void storedOrganizationSurvivesLoadRoundTrip() throws SQLException {
        organizations.addOrganizationToTheBase(sampleOrganization("Acme", "alice"), -1);

        List<Organization> loaded = loadAll();

        Organization acme = loaded.stream()
                .filter(organization -> organization.getName().equals("Acme"))
                .findFirst()
                .orElseThrow();
        assertEquals("alice", acme.getOwner());
        assertEquals(OrganizationType.COMMERCIAL, acme.getType());
        assertEquals(1000.0, acme.getAnnualTurnover());
        assertEquals("Saint Petersburg", acme.getOfficialAddress().getTown().getName());
    }

    @Test
    void ownershipCheckMatchesOnlyTheOwner() throws SQLException {
        organizations.addOrganizationToTheBase(sampleOrganization("Owned", "bob"), -1);
        int id = idOf("Owned");

        assertTrue(OrganizationsRepository.isOwnedBy(id, "bob"));
        assertFalse(OrganizationsRepository.isOwnedBy(id, "mallory"));
        assertFalse(OrganizationsRepository.isOwnedBy(-42, "bob"));
    }

    @Test
    void deletedOrganizationDisappearsFromTheCollection() throws SQLException {
        organizations.addOrganizationToTheBase(sampleOrganization("Doomed", "carol"), -1);
        int id = idOf("Doomed");

        organizations.deleteOrganizationFromDataBase(id);

        assertTrue(loadAll().stream().noneMatch(organization -> organization.getId() == id));
    }

    private static List<Organization> loadAll() throws SQLException {
        List<Organization> loaded = new ArrayList<>();
        OrganizationsRepository.loadCollection(loaded);
        return loaded;
    }

    private static int idOf(String name) throws SQLException {
        return loadAll().stream()
                .filter(organization -> organization.getName().equals(name))
                .findFirst()
                .orElseThrow()
                .getId();
    }

    private static Organization sampleOrganization(String name, String owner) {
        return new Organization(
                0,
                name,
                new Coordinates(10.5, 20.5),
                LocalDateTime.of(2020, 7, 1, 20, 29),
                1000.0,
                OrganizationType.COMMERCIAL,
                new Address("Main street", "191187", new Location(1f, 2f, "Saint Petersburg")),
                owner,
                "0x990000ff");
    }
}
