package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.server.core.OrganizationCollection;
import io.github.alialibekovich.collection.server.core.UserStore;

import java.sql.SQLException;

public class AddIfMinHandler extends AuthenticatedHandler {

    private final OrganizationCollection collection;

    public AddIfMinHandler(UserStore users, OrganizationCollection collection) {
        super(users);
        this.collection = collection;
    }

    @Override
    protected String handleAuthenticated(String login, String[] args, Object payload) throws SQLException {
        return collection.addIfMin((Organization) payload);
    }
}
