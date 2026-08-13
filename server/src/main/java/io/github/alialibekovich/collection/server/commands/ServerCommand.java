package io.github.alialibekovich.collection.server.commands;

import io.github.alialibekovich.collection.protocol.Command;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.sql.SQLException;

/**
 * Base class for the server-side representation of every command.
 *
 * <p>A deserialized command is executed against the shared collection state and
 * the database, and writes its answer back to the originating client through the
 * supplied datagram channel.</p>
 */
public abstract class ServerCommand implements Command {

    public abstract void execute(Object payload, DatagramChannel channel, SocketAddress socketAddress)
            throws IOException, SQLException;
}
