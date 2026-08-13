package io.github.alialibekovich.collection.server.db;

import io.github.alialibekovich.collection.model.Address;
import io.github.alialibekovich.collection.model.Coordinates;
import io.github.alialibekovich.collection.model.Location;
import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.model.OrganizationType;
import io.github.alialibekovich.collection.server.core.OrganizationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** JDBC implementation of {@link OrganizationStore} backed by PostgreSQL. */
public class OrganizationsRepository implements OrganizationStore {

    private static final Logger log = LoggerFactory.getLogger(OrganizationsRepository.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Connection connection;

    public OrganizationsRepository(Connection connection) throws SQLException {
        this.connection = connection;
        createTableIfAbsent();
    }

    private void createTableIfAbsent() throws SQLException {
        String ddl = "CREATE TABLE IF NOT EXISTS organizations "
                + "(id serial primary key not null,"
                + " owner TEXT NOT NULL, "
                + " name TEXT NOT NULL, "
                + " x DOUBLE PRECISION NOT NULL, "
                + " y DOUBLE PRECISION NOT NULL, "
                + " creationDate TEXT NOT NULL, "
                + " annualTurnover DOUBLE PRECISION NOT NULL,"
                + " organizationType TEXT NOT NULL, "
                + " street TEXT NOT NULL, "
                + " zipCode TEXT NOT NULL, "
                + " location_x DOUBLE PRECISION NOT NULL, "
                + " location_y DOUBLE PRECISION NOT NULL, "
                + " town TEXT NOT NULL,"
                + " color TEXT NOT NULL)";
        try (Statement statement = connection.createStatement()) {
            statement.execute(ddl);
        }
    }

    @Override
    public List<Organization> findAll() throws SQLException {
        List<Organization> organizations = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM organizations ORDER BY id")) {
            while (resultSet.next()) {
                organizations.add(new Organization(
                        resultSet.getInt(1),
                        resultSet.getString(3),
                        new Coordinates(resultSet.getDouble(4), resultSet.getDouble(5)),
                        LocalDateTime.parse(resultSet.getString(6), DATE_FORMAT),
                        resultSet.getDouble(7),
                        OrganizationType.valueOf(resultSet.getString(8)),
                        new Address(resultSet.getString(9), resultSet.getString(10),
                                new Location(resultSet.getFloat(11), resultSet.getFloat(12), resultSet.getString(13))),
                        resultSet.getString(2),
                        resultSet.getString(14)));
            }
        }
        return organizations;
    }

    @Override
    public void add(Organization organization, int id) {
        boolean withId = id != -1;
        String sql = withId
                ? "INSERT INTO organizations (id, owner, name, x, y, creationDate, annualTurnover, organizationType, street, zipCode, location_x, location_y, town, color) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                : "INSERT INTO organizations (owner, name, x, y, creationDate, annualTurnover, organizationType, street, zipCode, location_x, location_y, town, color) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 1;
            if (withId) {
                statement.setInt(i++, id);
            }
            statement.setString(i++, organization.getOwner());
            statement.setString(i++, organization.getName());
            statement.setDouble(i++, organization.getCoordinates().getX());
            statement.setDouble(i++, organization.getCoordinates().getY());
            statement.setString(i++, organization.getCreationDate().format(DATE_FORMAT));
            statement.setDouble(i++, organization.getAnnualTurnover());
            statement.setString(i++, organization.getType().getString());
            statement.setString(i++, organization.getOfficialAddress().getStreet());
            statement.setString(i++, organization.getOfficialAddress().getZipCode());
            statement.setDouble(i++, organization.getOfficialAddress().getTown().getX());
            statement.setDouble(i++, organization.getOfficialAddress().getTown().getY());
            statement.setString(i++, organization.getOfficialAddress().getTown().getName());
            statement.setString(i, organization.getColor());
            statement.execute();
        } catch (SQLException e) {
            log.error("Failed to insert an organization", e);
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM organizations WHERE id = ?")) {
            statement.setInt(1, id);
            statement.execute();
        }
    }

    @Override
    public boolean isOwnedBy(int id, String login) {
        String sql = "SELECT 1 FROM organizations WHERE id = ? AND owner = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, login);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            log.error("Failed to check the owner of organization {}", id, e);
            return false;
        }
    }

    @Override
    public void clearByOwner(String owner) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM organizations WHERE owner = ?")) {
            statement.setString(1, owner);
            statement.execute();
        }
    }
}
