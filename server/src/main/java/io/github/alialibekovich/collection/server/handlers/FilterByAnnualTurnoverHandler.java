package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.server.core.OrganizationCollection;
import io.github.alialibekovich.collection.server.core.UserStore;

public class FilterByAnnualTurnoverHandler extends AuthenticatedHandler {

    private final OrganizationCollection collection;

    public FilterByAnnualTurnoverHandler(UserStore users, OrganizationCollection collection) {
        super(users);
        this.collection = collection;
    }

    @Override
    protected String handleAuthenticated(String login, String[] args, Object payload) {
        try {
            return collection.filterByAnnualTurnover(Double.parseDouble(args[2]));
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            return "Неправильный аргумент команды! Команда не будет выполнена.";
        }
    }
}
