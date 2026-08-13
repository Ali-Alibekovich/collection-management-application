package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.server.core.OrganizationCollection;
import io.github.alialibekovich.collection.server.core.OrganizationStore;
import io.github.alialibekovich.collection.server.core.UserStore;

import java.sql.SQLException;

public class AddHandler extends AuthenticatedHandler {

    private final OrganizationStore store;
    private final OrganizationCollection collection;

    public AddHandler(UserStore users, OrganizationStore store, OrganizationCollection collection) {
        super(users);
        this.store = store;
        this.collection = collection;
    }

    @Override
    protected String handleAuthenticated(String login, String[] args, Object payload) throws SQLException {
        store.add((Organization) payload, -1);
        collection.reload();
        return "Организация добавлена в коллекцию.";
    }
}
