package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.server.core.OrganizationCollection;
import io.github.alialibekovich.collection.server.core.OrganizationStore;
import io.github.alialibekovich.collection.server.core.UserStore;

import java.sql.SQLException;

public class ClearHandler extends AuthenticatedHandler {

    private final OrganizationStore store;
    private final OrganizationCollection collection;

    public ClearHandler(UserStore users, OrganizationStore store, OrganizationCollection collection) {
        super(users);
        this.store = store;
        this.collection = collection;
    }

    @Override
    protected String handleAuthenticated(String login, String[] args, Object payload) throws SQLException {
        store.clearByOwner(login);
        collection.reload();
        return "Коллекция очищена";
    }
}
