package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.server.core.OrganizationCollection;
import io.github.alialibekovich.collection.server.core.OrganizationStore;
import io.github.alialibekovich.collection.server.core.UserStore;

import java.sql.SQLException;

public class UpdateHandler extends AuthenticatedHandler {

    private final OrganizationStore store;
    private final OrganizationCollection collection;

    public UpdateHandler(UserStore users, OrganizationStore store, OrganizationCollection collection) {
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
            return "ID был введён некорректно. Команда не выполнена.";
        }
        if (!store.isOwnedBy(id, login)) {
            return "Данный объект вам не принадлежит либо его не существует.";
        }
        if (!collection.doesExist(id)) {
            return "В коллекции нет организации с таким ID.";
        }
        Organization organization = (Organization) payload;
        store.delete(id);
        store.add(organization, id);
        collection.updateElement(organization, id);
        return "Организация обновлена.";
    }
}
