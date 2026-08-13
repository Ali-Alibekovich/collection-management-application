package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.server.core.OrganizationCollection;
import io.github.alialibekovich.collection.server.core.UserStore;
import io.github.alialibekovich.collection.server.util.ParserJson;

import java.sql.SQLException;

/** Answers with the whole collection as JSON (also used by {@code visualize}). */
public class ShowHandler extends AuthenticatedHandler {

    private final OrganizationCollection collection;

    public ShowHandler(UserStore users, OrganizationCollection collection) {
        super(users);
        this.collection = collection;
    }

    @Override
    protected String handleAuthenticated(String login, String[] args, Object payload) throws SQLException {
        collection.reload();
        return ParserJson.toJson(collection.snapshot());
    }
}
