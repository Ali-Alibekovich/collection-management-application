package io.github.alialibekovich.collection.server.core;

import io.github.alialibekovich.collection.model.Organization;

import java.util.ArrayList;
import java.util.List;

/** Simple in-memory {@link OrganizationStore} for exercising the collection. */
class InMemoryOrganizationStore implements OrganizationStore {

    private final List<Organization> rows = new ArrayList<>();

    @Override
    public synchronized List<Organization> findAll() {
        return new ArrayList<>(rows);
    }

    @Override
    public synchronized void add(Organization organization, int id) {
        rows.add(organization);
    }

    @Override
    public synchronized void delete(int id) {
        rows.removeIf(organization -> organization.getId() == id);
    }

    @Override
    public synchronized boolean isOwnedBy(int id, String login) {
        return rows.stream().anyMatch(o -> o.getId() == id && o.getOwner().equals(login));
    }

    @Override
    public synchronized void clearByOwner(String owner) {
        rows.removeIf(organization -> organization.getOwner().equals(owner));
    }
}
