package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.server.core.UserStore;
import io.github.alialibekovich.collection.server.util.WelcomeMailer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterHandlerTest {

    @Mock
    private UserStore users;
    @Mock
    private WelcomeMailer mailer;

    @Test
    void newUserIsRegistered() throws Exception {
        when(users.loginExists("alice")).thenReturn(false);
        when(users.colorTaken("0x990000ff")).thenReturn(false);

        String answer = new RegisterHandler(users, mailer)
                .handle("alice secret alice@example.com 0x990000ff", null);

        assertEquals("Успешно!", answer);
        verify(users).addUser("alice", "secret", "0x990000ff");
        verify(mailer).sendWelcome("alice", "alice@example.com");
    }

    @Test
    void duplicateLoginIsRejectedWithoutSideEffects() throws Exception {
        when(users.loginExists("alice")).thenReturn(true);

        String answer = new RegisterHandler(users, mailer)
                .handle("alice secret alice@example.com 0x990000ff", null);

        assertEquals("Пользователь с данным логином уже зарегистрирован!", answer);
        verify(users, never()).addUser(any(), any(), any());
    }
}
