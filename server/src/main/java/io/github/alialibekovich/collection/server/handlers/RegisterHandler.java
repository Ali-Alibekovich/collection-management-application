package io.github.alialibekovich.collection.server.handlers;

import io.github.alialibekovich.collection.server.core.UserStore;
import io.github.alialibekovich.collection.server.util.WelcomeMailer;

import javax.mail.MessagingException;
import java.sql.SQLException;

public class RegisterHandler implements CommandHandler {

    private final UserStore users;
    private final WelcomeMailer mailer;

    public RegisterHandler(UserStore users, WelcomeMailer mailer) {
        this.users = users;
        this.mailer = mailer;
    }

    @Override
    public String handle(String arg, Object payload) throws SQLException {
        String[] args = arg.split(" ");
        if (args.length < 4) {
            return "Пользователь с данным логином уже зарегистрирован!";
        }
        String login = args[0];
        String password = args[1];
        String mail = args[2];
        String color = args[3];
        if (users.loginExists(login) || users.colorTaken(color)) {
            return "Пользователь с данным логином уже зарегистрирован!";
        }
        users.addUser(login, password, color);
        try {
            mailer.sendWelcome(login, mail);
            return "Успешно!";
        } catch (MessagingException e) {
            return "Успешно, но приветственное письмо не было отправлено из-за неполадок с подключением!";
        }
    }
}
