package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.server.core.UserStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginHandlerTest {

    @Mock
    private UserStore users;

    @Test
    void validCredentialsAnswerWithSuccessAndColor() {
        when(users.checkCredentials("alice", "secret")).thenReturn(true);
        when(users.getColor("alice")).thenReturn("0x990000ff");

        assertEquals("Успешно! 0x990000ff", new LoginHandler(users).handle("alice secret", null));
    }

    @Test
    void invalidCredentialsAnswerWithFailure() {
        when(users.checkCredentials("alice", "wrong")).thenReturn(false);
        when(users.getColor("alice")).thenReturn("0xFFFFFFff");

        assertEquals("Не_успешно! 0xFFFFFFff", new LoginHandler(users).handle("alice wrong", null));
    }
}
