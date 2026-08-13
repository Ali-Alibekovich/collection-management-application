package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.server.core.UserStore;

import java.sql.SQLException;

/**
 * Template method for commands that require credentials: the argument starts
 * with {@code "<login> <password> ..."}; the pair is verified before the
 * command-specific logic runs.
 */
public abstract class AuthenticatedHandler implements CommandHandler {

    static final String AUTH_FAILED = "Пользователь не прошел проверку.";

    private final UserStore users;

    protected AuthenticatedHandler(UserStore users) {
        this.users = users;
    }

    @Override
    public final String handle(String arg, Object payload) throws SQLException {
        String[] args = arg.split(" ");
        if (args.length < 2 || !users.checkCredentials(args[0], args[1])) {
            return AUTH_FAILED;
        }
        return handleAuthenticated(args[0], args, payload);
    }

    /** @param login the verified caller
     *  @param args  the full split argument, {@code args[0]}=login, {@code args[1]}=password */
    protected abstract String handleAuthenticated(String login, String[] args, Object payload) throws SQLException;
}
