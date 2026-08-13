package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.server.core.UserStore;

public class LoginHandler implements CommandHandler {

    private final UserStore users;

    public LoginHandler(UserStore users) {
        this.users = users;
    }

    @Override
    public String handle(String arg, Object payload) {
        String[] args = arg.split(" ");
        boolean valid = args.length >= 2 && users.checkCredentials(args[0], args[1]);
        return (valid ? "Успешно!" : "Не_успешно!") + " " + users.getColor(args[0]);
    }
}
