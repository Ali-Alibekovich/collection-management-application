package io.github.alialibekovich.collection.server.handlers;

import java.sql.SQLException;

/**
 * Strategy for one wire command. Handlers are pure request→answer functions:
 * transport (receiving datagrams, sending the answer) stays in the net layer,
 * which is what makes every handler unit-testable with plain mocks.
 */
public interface CommandHandler {

    /** @param arg     the string argument of the envelope ({@code ""} if absent)
     *  @param payload the object payload of the envelope ({@code null} if absent)
     *  @return the answer to send back to the client */
    String handle(String arg, Object payload) throws SQLException;
}
