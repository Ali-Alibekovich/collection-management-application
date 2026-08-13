package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.model.Address;
import io.github.alialibekovich.collection.model.Coordinates;
import io.github.alialibekovich.collection.model.Location;
import io.github.alialibekovich.collection.model.Organization;
import io.github.alialibekovich.collection.model.OrganizationType;
import io.github.alialibekovich.collection.server.core.OrganizationCollection;
import io.github.alialibekovich.collection.server.core.OrganizationStore;
import io.github.alialibekovich.collection.server.core.UserStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateHandlerTest {

    @Mock
    private UserStore users;
    @Mock
    private OrganizationStore store;
    @Mock
    private OrganizationCollection collection;

    @Test
    void authenticationIsCheckedFirst() throws Exception {
        when(users.checkCredentials("alice", "wrong")).thenReturn(false);

        String answer = new UpdateHandler(users, store, collection).handle("alice wrong 5", payload());

        assertEquals("Пользователь не прошел проверку.", answer);
        verify(store, never()).delete(anyInt());
    }

    @Test
    void malformedIdIsRejected() throws Exception {
        when(users.checkCredentials("alice", "secret")).thenReturn(true);

        String answer = new UpdateHandler(users, store, collection).handle("alice secret abc", payload());

        assertEquals("ID был введён некорректно. Команда не выполнена.", answer);
    }

    @Test
    void foreignOrganizationIsRejected() throws Exception {
        when(users.checkCredentials("alice", "secret")).thenReturn(true);
        when(store.isOwnedBy(5, "alice")).thenReturn(false);

        String answer = new UpdateHandler(users, store, collection).handle("alice secret 5", payload());

        assertEquals("Данный объект вам не принадлежит либо его не существует.", answer);
        verify(store, never()).delete(anyInt());
    }

    @Test
    void ownedOrganizationIsReplaced() throws Exception {
        when(users.checkCredentials("alice", "secret")).thenReturn(true);
        when(store.isOwnedBy(5, "alice")).thenReturn(true);
        when(collection.doesExist(5)).thenReturn(true);

        String answer = new UpdateHandler(users, store, collection).handle("alice secret 5", payload());

        assertEquals("Организация обновлена.", answer);
        verify(store).delete(5);
        verify(store).add(any(Organization.class), anyInt());
    }

    private static Organization payload() {
        return new Organization(5, "Acme", new Coordinates(1.0, 2.0),
                LocalDateTime.of(2020, 7, 1, 20, 29), 100.0, OrganizationType.COMMERCIAL,
                new Address("s", "z", new Location(1f, 2f, "t")), "alice", "0x990000ff");
    }
}
