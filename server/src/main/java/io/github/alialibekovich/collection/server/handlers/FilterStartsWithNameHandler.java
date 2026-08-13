package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.server.core.OrganizationCollection;
import io.github.alialibekovich.collection.server.core.UserStore;

public class FilterStartsWithNameHandler extends AuthenticatedHandler {

    private final OrganizationCollection collection;

    public FilterStartsWithNameHandler(UserStore users, OrganizationCollection collection) {
        super(users);
        this.collection = collection;
    }

    @Override
    protected String handleAuthenticated(String login, String[] args, Object payload) {
        if (args.length < 3) {
            return "Неправильный аргумент команды! Команда не будет выполнена.";
        }
        return collection.filterStartsWithName(args[2]);
    }
}
