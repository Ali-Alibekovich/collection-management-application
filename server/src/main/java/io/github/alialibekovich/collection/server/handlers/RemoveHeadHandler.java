package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.server.core.OrganizationCollection;
import io.github.alialibekovich.collection.server.core.UserStore;

public class RemoveHeadHandler extends AuthenticatedHandler {

    private final OrganizationCollection collection;

    public RemoveHeadHandler(UserStore users, OrganizationCollection collection) {
        super(users);
        this.collection = collection;
    }

    @Override
    protected String handleAuthenticated(String login, String[] args, Object payload) {
        return collection.removeHead(login);
    }
}
