package io.github.alialibekovich.collection.server.core;

import io.github.alialibekovich.collection.model.Organization;

import java.sql.SQLException;
import java.util.List;

/** Durable storage of organizations, free of JDBC details. */
public interface OrganizationStore {

    List<Organization> findAll() throws SQLException;

    /** @param id an existing id to keep, or {@code -1} to let the store assign one */
    void add(Organization organization, int id);

    void delete(int id) throws SQLException;

    boolean isOwnedBy(int id, String login);

    void clearByOwner(String owner) throws SQLException;
}
