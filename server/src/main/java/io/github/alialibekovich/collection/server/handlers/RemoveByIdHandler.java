package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.server.core.OrganizationCollection;
import io.github.alialibekovich.collection.server.core.OrganizationStore;
import io.github.alialibekovich.collection.server.core.UserStore;

import java.sql.SQLException;

public class RemoveByIdHandler extends AuthenticatedHandler {

    private final OrganizationStore store;
    private final OrganizationCollection collection;

    public RemoveByIdHandler(UserStore users, OrganizationStore store, OrganizationCollection collection) {
        super(users);
        this.store = store;
        this.collection = collection;
    }

    @Override
    protected String handleAuthenticated(String login, String[] args, Object payload) throws SQLException {
        int id;
        try {
            id = Integer.parseInt(args[2]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            return "Неправильный аргумент команды! Команда не будет выполнена.";
        }
        if (!store.isOwnedBy(id, login)) {
            return "Данный объект не принадлежит вам.";
        }
        if (!collection.doesExist(id)) {
            return "Такого элемента нет в коллекции.";
        }
        store.delete(id);
        collection.removeElement(id);
        return "Элемент удалён.";
    }
}
