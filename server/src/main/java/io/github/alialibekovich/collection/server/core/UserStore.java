package io.github.alialibekovich.collection.server.core;

import java.sql.SQLException;

/** What the domain needs to know about user accounts, free of JDBC details. */
public interface UserStore {

    boolean checkCredentials(String login, String password);

    boolean loginExists(String login) throws SQLException;

    boolean colorTaken(String color) throws SQLException;

    void addUser(String login, String password, String color) throws SQLException;

    String getColor(String login);
}
